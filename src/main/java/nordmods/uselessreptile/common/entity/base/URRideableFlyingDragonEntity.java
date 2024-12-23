package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.FlyingDragonNavigation;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.network.LiftoffParticlesS2CPacket;

public abstract class URRideableFlyingDragonEntity extends URRideableDragonEntity implements FlyingDragon {
    protected final int maxInAirTimer = 600;
    protected float pitchLimitAir = 90;
    private int flyUpWindow;
    private boolean jumpWasPressed;
    protected float tiltProgress;
    protected boolean shouldGlide;
    private int glideTimer = 100;
    private boolean forceFlight = false;

    protected URRideableFlyingDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new FlyingDragonMoveControl<>(this);
        navigation = new FlyingDragonNavigation<>(this, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(GLIDING, false);
        builder.add(FLYING, false);
        builder.add(TILT_STATE, (byte)0);//1 - вверх, 2 - вниз, 0 - летит прямо
        builder.add(IN_AIR_TIMER, 0);
    }

    public static final TrackedData<Boolean> GLIDING = DataTracker.registerData(URRideableFlyingDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> FLYING = DataTracker.registerData(URRideableFlyingDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Byte> TILT_STATE = DataTracker.registerData(URRideableFlyingDragonEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> IN_AIR_TIMER = DataTracker.registerData(URRideableFlyingDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public int getInAirTimer() {return dataTracker.get(IN_AIR_TIMER);}
    public void setInAirTimer(int state) {dataTracker.set(IN_AIR_TIMER, state);}

    public boolean isGliding() {return dataTracker.get(GLIDING);}
    public void setGliding(boolean state) {dataTracker.set(GLIDING, state);}

    public boolean isFlying() {return dataTracker.get(FLYING);}
    public void setFlying (boolean state) {dataTracker.set(FLYING, state);}

    public byte getTiltState() {return dataTracker.get(TILT_STATE);}
    public void setTiltState(byte state) {dataTracker.set(TILT_STATE, state);}

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putBoolean("IsFlying", isFlying());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        setFlying(tag.getBoolean("IsFlying"));
    }

    @Override
    public void tick() {
        super.tick();
        updateTiltProgress();

        if (getWorld().isClient()) {
            glideTimer--;
            shouldGlide = glideTimer < 0 && getAccelerationDuration()/getMaxAccelerationDuration() > 0.9;
            if (glideTimer < -50 - getRandom().nextInt(100)) glideTimer = 100 + getRandom().nextInt(100);
        }
        checkForceFlight();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (!isAlive()) return;

        if ((!isMoving() || isFlying())) setSprinting(false);
        if (isSprinting()) setSpeedMod(1.5f);
        else if (isMovingBackwards() && isFlying()) setSpeedMod(0.6f);
        else setSpeedMod(1f);
        float speed = isFlying() ? (float) getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED) : (float) getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        setMovementSpeed(speed * getSpeedModifier());

        if (isOnGround()) setFlying(false);
        setNoGravity(isFlying());

        if (canBeControlledByRider()) {
            LivingEntity rider = getControllingPassenger();
            if (rider instanceof PlayerEntity player) super.travel(getControlledMovementInput(player, movementInput));
        } else {
            if (isFlying()) {
                if (getInAirTimer() < maxInAirTimer) setInAirTimer(getInAirTimer() + 1);
                super.travel(movementInput);
            } else {
                setInAirTimer(0);
                super.travel(movementInput);
            }

        }
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity rider, Vec3d movementInput) {
        boolean isInputGiven = isMoveBackPressed() || isMoveForwardPressed() || isDownPressed() || isJumpPressed();
        //The acceleration logic. Looks like a mess, but it's still understandable I guess
        int accelerationDuration = getAccelerationDuration();
        if (accelerationDuration < 0) accelerationDuration = 0;
        float accelerationModifier = (float) accelerationDuration / getMaxAccelerationDuration();
        if (accelerationModifier > 1.5) accelerationModifier = 1.5f;
        if (isInputGiven && getTurningState() == 0) accelerationDuration++;
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
        setPitch(MathHelper.clamp(rider.getPitch(), -getPitchLimit(), getPitchLimit()));
        if (!isFlying()) {
            double landSpeed = rider.forwardSpeed * getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (isSprintPressed()) setSprinting(true);
            if (isMovingBackwards() && (isMoveBackPressed() || isMoveBackPressed())) setSprinting(false);
            setRotation(rider);

            if (isJumpPressed() && !jumpWasPressed) {
                if (flyUpWindow <= 0) {
                    jumpWasPressed = true;
                    flyUpWindow = 10;
                    if (isOnGround()) jump();
                } else {
                    startToFly();
                    flyUpWindow = 0;
                }
            } else if (!isJumpPressed() && jumpWasPressed) jumpWasPressed = false;
            if (flyUpWindow > 0) flyUpWindow--;
            else jumpWasPressed = false;
            //adding some extra small number to Y velocity so on client it checks isOnGround() correctly
            return new Vec3d(0, movementInput.y - 0.001, landSpeed);
        } else {
            double flyingSpeed = rider.forwardSpeed * getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED);
            float pitchSpeed = 2;
            setRotation(rider);
            float verticalSpeed = 0F;
            setGliding(accelerationModifier > 1);

            if (isJumpPressed()) {
                verticalSpeed = getVerticalSpeed();
                setTiltState((byte) 1);
                setGliding(false);
                if (!isMovingBackwards() && isMoving() && getPitch() > -getPitchLimit() && !isDownPressed())
                    setPitch(getPitch() - pitchSpeed);
            }
            if (isDownPressed()) {
                verticalSpeed = -getVerticalSpeed() * 1.3f;
                setTiltState((byte) 2);
                if (!isMovingBackwards() && isMoving() && getPitch() < getPitchLimit())
                    setPitch(getPitch() + pitchSpeed);
            }
            float currentVerticalSpeed = (float) getVelocity().getY();
            if (!(isJumpPressed() || isDownPressed())) {
                if (getPitch() != 0) {
                    if (getPitch() < 0 && getPitch() < -pitchSpeed) setPitch(getPitch() + pitchSpeed);
                    if (getPitch() > 0 && getPitch() > pitchSpeed) setPitch(getPitch() - pitchSpeed);
                    if (getPitch() < pitchSpeed && getPitch() > -pitchSpeed) setPitch(0);
                }
                if (currentVerticalSpeed != 0) verticalSpeed = currentVerticalSpeed * -0.5F;
                setTiltState((byte) 0);
            }

            return new Vec3d(0, verticalSpeed * MathHelper.clamp(accelerationModifier, 0.25, 1.5), flyingSpeed * accelerationModifier * 2.5F);
        }
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {return false;}

    @Override
    public boolean isFlappingWings() {return isFlying();}

    public void startToFly() {
        jump();
        setFlying(true);
        setAccelerationDuration(getAccelerationDuration() / 10);
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) LiftoffParticlesS2CPacket.send(player, this);
    }

    @Override
    public float getRotationSpeed() {
        if (isFlying()) return getFlyingRotationSpeed() * getMovementSpeedModifier() / 2f;
        return super.getRotationSpeed();
    }

    @Override
    public float getFlyingRotationSpeed() {
        return (float) getAttributeValue(URAttributes.DRAGON_FLYING_ROTATION_SPEED);
    }

    @Override
    public float getPitchLimit() {
        if (isFlying() && isMoving() && !isMovingBackwards()) return pitchLimitAir;
        return pitchLimitGround;
    }

    @Override
    protected float getMovementSpeedModifier() {
        if (!isFlying()) return super.getMovementSpeedModifier();
        double baseSpeed = getAttributeBaseValue(EntityAttributes.GENERIC_FLYING_SPEED);
        double speed = getAttributeBaseValue(EntityAttributes.GENERIC_FLYING_SPEED);
        return (float) (speed / baseSpeed);
    }

    public int getMaxInAirTimer() {
        return maxInAirTimer;
    }

    protected float getOffGroundSpeed() {
        float movementSpeed = getMovementSpeed();
        return hasControllingPassenger() ? movementSpeed * 0.1f : movementSpeed *  0.14f;
    }

    private void updateTiltProgress() {
        switch (getTiltState()) {
            case 1 -> {
                if (tiltProgress < TRANSITION_TICKS) tiltProgress++;
            }
            case 2 -> {
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
    public FlyingDragonMoveControl<? extends FlyingDragon> getMoveControl() {
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
}
