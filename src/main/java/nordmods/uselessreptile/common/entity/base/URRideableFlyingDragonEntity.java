package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonBodyRotationControl;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.FlyingDragonAirNavigation;
import nordmods.uselessreptile.common.entity.ai.navigation.FlyingDragonLandNavigation;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.UREntityDataSerializers;
import nordmods.uselessreptile.common.network.s2c.LiftoffParticlesPayload;
import nordmods.uselessreptile.common.network.c2s.RequestLiftoffPayload;
import org.jspecify.annotations.NonNull;

public abstract class URRideableFlyingDragonEntity extends URRideableDragonEntity implements FlyingDragon {
    protected final int maxInAirTimer = 600;
    protected float pitchLimitAir = 90;
    private int flyUpWindow;
    private boolean jumpWasPressed;
    protected float tiltProgress;
    private int glideTimer = 100;
    private boolean forceFlight = false;
    private final FlyingDragonLandNavigation<URRideableFlyingDragonEntity> landNavigation;
    private final FlyingDragonAirNavigation<URRideableFlyingDragonEntity> airNavigation;

    protected URRideableFlyingDragonEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        moveControl = new FlyingDragonMoveControl<>(this);
        landNavigation = new FlyingDragonLandNavigation<>(this, level());
        airNavigation = new FlyingDragonAirNavigation<>(this, level());
        navigation = landNavigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
        builder.define(FLY_GLIDING, false);
        builder.define(TILT_STATE, TiltState.NONE);//1 - вверх, 2 - вниз, 0 - летит прямо
        builder.define(IN_AIR_TIMER, 0);
    }
    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(URRideableFlyingDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FLY_GLIDING = SynchedEntityData.defineId(URRideableFlyingDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<TiltState> TILT_STATE = SynchedEntityData.defineId(URRideableFlyingDragonEntity.class, UREntityDataSerializers.TILT_STATE);
    public static final EntityDataAccessor<Integer> IN_AIR_TIMER = SynchedEntityData.defineId(URRideableFlyingDragonEntity.class, EntityDataSerializers.INT);


    public int getInAirTimer() {return entityData.get(IN_AIR_TIMER);}
    public void setInAirTimer(int state) {entityData.set(IN_AIR_TIMER, state);}

    public boolean isFlying() {return entityData.get(FLYING);}
    public void setFlying (boolean state) {entityData.set(FLYING, state);}

    public boolean isFlyGliding() {return entityData.get(FLY_GLIDING);}
    public void setFlyGliding (boolean state) {entityData.set(FLY_GLIDING, state);}

    public TiltState getTiltState() {return entityData.get(TILT_STATE);}
    public void setTiltState(TiltState state) {entityData.set(TILT_STATE, state);}

    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsFlying", isFlying());
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        setFlying(tag.getBooleanOr("IsFlying", false));
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (JUMP_PRESSED.equals(data)) {
            if (level().isClientSide() && getControllingPassenger() instanceof LocalPlayer) {
                if (isJumpPressed() && !jumpWasPressed) {
                    if (flyUpWindow <= 0) {
                        jumpWasPressed = true;
                        flyUpWindow = 10;
                    } else {
                        ClientPlayNetworking.send(new RequestLiftoffPayload(getId()));
                        flyUpWindow = 0;
                    }
                } else if (!isJumpPressed() && jumpWasPressed) jumpWasPressed = false;
                else jumpWasPressed = false;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateTiltProgress();

        if (!level().isClientSide()) {
            glideTimer--;
            float accelerationModifier = getAccelerationDuration()/getMaxAccelerationDuration();
            setFlyGliding(accelerationModifier > 1 || glideTimer < 0 && accelerationModifier > 0.9);
            if (glideTimer < -50 - getRandom().nextInt(100)) glideTimer = 100 + getRandom().nextInt(100);
        } else {
            if (flyUpWindow > 0) flyUpWindow--;
        }
        checkForceFlight();

        updateNavigation();
    }

    private void updateNavigation() {
        PathNavigation current = navigation;
        navigation = isFlying() ? airNavigation : landNavigation;
        if (current != navigation) current.stop();
    }

    @Override
    public void travel(@NonNull Vec3 movementInput) {
        if (!isAlive()) return;
        if (!hasControllingPassenger()) {
            if (isFlying()) if (getInAirTimer() < maxInAirTimer) setInAirTimer(getInAirTimer() + 1);
            else setInAirTimer(0);
        }
        super.travel(movementInput);
    }

    @Override
    public void updateMovementModifiers() {
        if ((!isMoving() || isFlying())) setSprinting(false);
        float speedModifier = 1;
        if (isSprinting()) speedModifier = sprintSpeedModifier;
        else if (isMovingBackwards()) speedModifier = backwardSpeedModifier;

        AttributeInstance instance = isFlying() ?getAttribute(Attributes.FLYING_SPEED) : getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedModifier != 1f) {
            AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_BONUS, speedModifier - 1f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            if (!instance.hasModifier(modifier.id())) instance.addTransientModifier(modifier);
            else instance.addOrUpdateTransientModifier(modifier);
        } else instance.removeModifier(SPEED_MODIFIER_BONUS);

        float speed = isFlying() ? (float) getAttributeValue(Attributes.FLYING_SPEED) : (float) getAttributeValue(Attributes.MOVEMENT_SPEED);
        setSpeed(speed * speedModifier);

        if (!level().isClientSide() && onGround())
            setFlying(false);
        setNoGravity(isFlying());
    }

    public Vec3 updateMovementInput(Player rider, Vec3 movementInput) {
        zza = 0;
        if (isMoveForwardPressed()) zza = 1;
        if (isMoveBackPressed()) zza = -1;

        boolean isInputGiven = isMoveBackPressed() || isMoveForwardPressed() || isDownPressed() || isJumpPressed();
        //The acceleration logic. Looks like a mess, but it's still understandable I guess
        int accelerationDuration = getAccelerationDuration();
        if (accelerationDuration < 0) accelerationDuration = 0;
        float accelerationModifier = (float) accelerationDuration / getMaxAccelerationDuration();
        if (accelerationModifier > 1.5) accelerationModifier = 1.5f;
        if (isInputGiven && getTurningState() == TurningState.NONE) accelerationDuration++;
        if (isJumpPressed() && !isDownPressed() && accelerationDuration > getMaxAccelerationDuration() * 0.4)
            accelerationDuration -= 2;
        if (isDownPressed() && accelerationDuration < getMaxAccelerationDuration() * 3 && isFlying())
            accelerationDuration += 2;
        if (!(isMoveBackPressed() || isMoveForwardPressed()) || (isMoveBackPressed() && isMoveForwardPressed())) {
            accelerationDuration /= 2;
            if (!isMoving()) accelerationDuration = 0;
        }
        if (isMoveBackPressed() && !isMoveForwardPressed() && accelerationDuration > getMaxAccelerationDuration() * 0.25)
            accelerationDuration -= 2;
        if (!isDownPressed() && accelerationDuration > getMaxAccelerationDuration()) {
            accelerationDuration -= 2;
            if (isJumpPressed()) accelerationDuration -= 2;
        }
        setAccelerationDuration(accelerationDuration);

        setMovingBackwards(isMoveBackPressed() || (!isMoveForwardPressed() && !isMoveBackPressed() && isMoving()));
        setXRot(Mth.clamp(rider.getXRot(), -getMaxHeadXRot(), getMaxHeadXRot()));
        if (!isFlying()) {
            double landSpeed = zza * getAttributeValue(Attributes.MOVEMENT_SPEED);
            if (isSprintPressed())
                setSprinting(true);
            if (isMovingBackwards() && (isMoveBackPressed() || isMoveBackPressed()))
                setSprinting(false);
            setRotation(rider);

            if (isJumpPressed() && !jumpWasPressed) {
                jumpWasPressed = true;
                if (onGround()) jumpFromGround();
            } else if (!isJumpPressed() && jumpWasPressed) jumpWasPressed = false;
            //adding some extra small number to Y velocity so on client it checks isOnGround() correctly
            return new Vec3(0, movementInput.y - 0.001, landSpeed);
        } else {
            double flyingSpeed = zza * getAttributeValue(Attributes.FLYING_SPEED);
            float pitchSpeed = 2;
            setRotation(rider);
            float verticalSpeed = 0F;

            if (isJumpPressed()) {
                verticalSpeed = getVerticalSpeed();
                setTiltState(TiltState.UP);
                if (!isMovingBackwards() && isMoving() && getXRot() > -getMaxHeadXRot() && !isDownPressed())
                    setXRot(getXRot() - pitchSpeed);
            }
            if (isDownPressed()) {
                verticalSpeed = -getVerticalSpeed() * 1.3f;
                setTiltState(TiltState.DOWN);
                if (!isMovingBackwards() && isMoving() && getXRot() < getMaxHeadXRot())
                    setXRot(getXRot() + pitchSpeed);
            }
            float currentVerticalSpeed = (float) getDeltaMovement().y();
            if (!(isJumpPressed() || isDownPressed())) {
                if (getXRot() != 0) {
                    if (getXRot() < 0 && getXRot() < -pitchSpeed) setXRot(getXRot() + pitchSpeed);
                    if (getXRot() > 0 && getXRot() > pitchSpeed) setXRot(getXRot() - pitchSpeed);
                    if (getXRot() < pitchSpeed && getXRot() > -pitchSpeed) setXRot(0);
                }
                if (currentVerticalSpeed != 0) verticalSpeed = currentVerticalSpeed * -0.5F;
                setTiltState(TiltState.NONE);
            }
            return new Vec3(0, verticalSpeed * Mth.clamp(accelerationModifier, 0.25, 1.5), flyingSpeed * accelerationModifier * 2.5F);
        }
    }

    protected void updateRiderBonus(boolean hasRider) {
        super.updateRiderBonus(hasRider);

        float mult = URMobAttributesConfig.getConfig().riddenDragonFlyingSpeedMultiplier;
        if (mult == 1) return;

        AttributeInstance entityAttributeInstance = getAttribute(Attributes.FLYING_SPEED);
        if (hasRider) {
            if (!entityAttributeInstance.hasModifier(RIDER_BONUS))
                entityAttributeInstance.addTransientModifier(new AttributeModifier(RIDER_BONUS, mult, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        } else entityAttributeInstance.removeModifier(RIDER_BONUS);
    }

    @Override
    protected int calculateFallDamage(double fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, @NonNull DamageSource damageSource) {return false;}

    @Override
    public boolean isFlapping() {return isFlying();}

    public void startToFly() {
        jumpFromGround();
        if (level() instanceof ServerLevel world) {
            setAccelerationDuration(getAccelerationDuration() / 10);
            setFlying(true);
            for (ServerPlayer player : PlayerLookup.tracking(world, blockPosition())) LiftoffParticlesPayload.send(player, this);
        }
    }

    @Override
    public int getHeadRotSpeed() {
        if (isFlying()) return (int) (getFlyingRotationSpeed() * getMovementSpeedModifier());
        return super.getHeadRotSpeed();
    }

    @Override
    public float getFlyingRotationSpeed() {
        return (float) getAttributeValue(URAttributes.DRAGON_FLYING_ROTATION_SPEED);
    }

    @Override
    public int getMaxHeadXRot() {
        if (isFlying() && isMoving() && !isMovingBackwards()) return (int) pitchLimitAir;
        return (int) pitchLimitGround;
    }

    @Override
    protected float getMovementSpeedModifier() {
        if (!isFlying()) return super.getMovementSpeedModifier();
        double baseSpeed = getAttributeBaseValue(Attributes.FLYING_SPEED);
        double speed = getAttributeValue(Attributes.FLYING_SPEED);
        return (float) (speed / baseSpeed);
    }

    public int getMaxInAirTimer() {
        return maxInAirTimer;
    }

    protected float getFlyingSpeed() {
        float movementSpeed = getSpeed();
        return hasControllingPassenger() ? movementSpeed * 0.1f : movementSpeed *  0.14f;
    }

    private void updateTiltProgress() {
        switch (getTiltState()) {
            case UP -> {
                if (tiltProgress < TRANSITION_TICKS) tiltProgress++;
            }
            case DOWN -> {
                if (tiltProgress > -TRANSITION_TICKS) tiltProgress--;
            }
            default -> {
                if (tiltProgress != 0) {
                    if (tiltProgress > 0) tiltProgress--;
                    else  tiltProgress++;
                }
            }
        }
    }

    @Override
    public float getVerticalSpeed() {
        return (float) getAttributeValue(URAttributes.DRAGON_VERTICAL_SPEED);
    }

    @Override
    public @NonNull FlyingDragonMoveControl<? extends FlyingDragon> getMoveControl() {
        return (FlyingDragonMoveControl<?>) moveControl;
    }

    public void forceFlightNextTick() {
        forceFlight = true;
    }

    private void checkForceFlight() {
        if (forceFlight) {
            forceFlight = false;
            startToFly();
        }
    }

    @Override
    public boolean hasVerticalInput() {
        return isFlying() && !freeLook();
    }

    @Override
    public boolean shouldFlyDown() {
        return getInAirTimer() >= getMaxInAirTimer();
    }

    @Override
    protected @NonNull BodyRotationControl createBodyControl() {
        return new FlyingDragonBodyRotationControl<>(this);
    }
}
