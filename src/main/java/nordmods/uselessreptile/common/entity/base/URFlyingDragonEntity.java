package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.navigation.FlyingDragonAirNavigation;
import nordmods.uselessreptile.common.entity.ai.navigation.FlyingDragonLandNavigation;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.network.s2c.LiftoffParticlesPayload;
import org.jspecify.annotations.NonNull;

public abstract class URFlyingDragonEntity extends URDragonEntity implements FlyingDragon {
    protected final int maxInAirTimer = 600;
    protected float pitchLimitAir = 90;
    protected float tiltProgress;
    private int glideTimer = 100;
    private boolean forceFlight = false;
    private final FlyingDragonLandNavigation<URFlyingDragonEntity> landNavigation;
    private final FlyingDragonAirNavigation<URFlyingDragonEntity> airNavigation;

    protected URFlyingDragonEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
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
        builder.define(TILT_STATE, (byte)0);//1 - вверх, 2 - вниз, 0 - летит прямо
        builder.define(IN_AIR_TIMER, 0);
    }

    public static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(URFlyingDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FLY_GLIDING = SynchedEntityData.defineId(URFlyingDragonEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> TILT_STATE = SynchedEntityData.defineId(URFlyingDragonEntity.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Integer> IN_AIR_TIMER = SynchedEntityData.defineId(URFlyingDragonEntity.class, EntityDataSerializers.INT);


    public int getInAirTimer() {return entityData.get(IN_AIR_TIMER);}
    public void setInAirTimer(int state) {entityData.set(IN_AIR_TIMER, state);}

    public boolean isFlyGliding() {return entityData.get(FLY_GLIDING);}
    public void setFlyGliding (boolean state) {entityData.set(FLY_GLIDING, state);}

    public boolean isFlying() {return entityData.get(FLYING);}
    public void setFlying (boolean state) {entityData.set(FLYING, state);}

    public byte getTiltState() {return entityData.get(TILT_STATE);}
    public void setTiltState(byte state) {entityData.set(TILT_STATE, state);}

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
        if (!level().isClientSide())
            if (FLYING.equals(data)) getNavigation().recomputePath();
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
        double baseSpeed = getAttributeBaseValue(Attributes.FLYING_SPEED);
        double speed = getAttributeBaseValue(Attributes.FLYING_SPEED);
        return (float) (speed / baseSpeed);
    }

    public void startToFly() {
        jumpFromGround();
        if (level() instanceof ServerLevel world) {
            setAccelerationDuration(getAccelerationDuration() / 10);
            setFlying(true);
            for (ServerPlayer player : PlayerLookup.tracking(world, blockPosition())) LiftoffParticlesPayload.send(player, this);
        }
    }

    public int getMaxInAirTimer() {
        return maxInAirTimer;
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
        if (isFlying()) if (getInAirTimer() < maxInAirTimer) setInAirTimer(getInAirTimer() + 1);
        else setInAirTimer(0);
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

        if (!level().isClientSide() && (onGround() && !isUnderWater() || isPassenger()))
             setFlying(false);
        setNoGravity(isFlying());
    }

    @Override
    protected int calculateFallDamage(double fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageMultiplier, @NonNull DamageSource damageSource) {return false;}

    protected float getFlyingSpeed() {
        return getSpeed() *  0.14f;
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
    public boolean shouldFlyDown() {
        return getInAirTimer() >= getMaxInAirTimer();
    }
}
