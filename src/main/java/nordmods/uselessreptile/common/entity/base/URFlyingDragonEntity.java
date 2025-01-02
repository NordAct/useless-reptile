package nordmods.uselessreptile.common.entity.base;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
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

public abstract class URFlyingDragonEntity extends URDragonEntity implements FlyingDragon {

    protected final int maxInAirTimer = 600;
    protected float pitchLimitAir = 90;
    protected float tiltProgress;
    protected boolean shouldGlide;
    private int glideTimer = 100;
    private boolean forceFlight = false;

    protected URFlyingDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new FlyingDragonMoveControl<>(this);
        navigation = new FlyingDragonNavigation<>(this, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FLYING, false);
        builder.add(TILT_STATE, (byte)0);//1 - вверх, 2 - вниз, 0 - летит прямо
        builder.add(IN_AIR_TIMER, 0);
    }

    public static final TrackedData<Boolean> FLYING = DataTracker.registerData(URFlyingDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Byte> TILT_STATE = DataTracker.registerData(URFlyingDragonEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> IN_AIR_TIMER = DataTracker.registerData(URFlyingDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public int getInAirTimer() {return dataTracker.get(IN_AIR_TIMER);}
    public void setInAirTimer(int state) {dataTracker.set(IN_AIR_TIMER, state);}

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
        double baseSpeed = getAttributeBaseValue(EntityAttributes.FLYING_SPEED);
        double speed = getAttributeBaseValue(EntityAttributes.FLYING_SPEED);
        return (float) (speed / baseSpeed);
    }

    public void startToFly() {
        jump();
        if (getWorld() instanceof ServerWorld world) {
            setAccelerationDuration(getAccelerationDuration() / 10);
            setFlying(true);
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) LiftoffParticlesS2CPacket.send(player, this);
        }
    }

    public int getMaxInAirTimer() {
        return maxInAirTimer;
    }

    @Override
    public void tick() {
        super.tick();
        updateTiltProgress();

        if (getWorld().isClient()) {
            glideTimer--;
            float accelerationMod = getAccelerationDuration()/getMaxAccelerationDuration();
            shouldGlide = accelerationMod > 1 || glideTimer < 0 && accelerationMod > 0.9;
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
        float speed = isFlying() ? (float) getAttributeValue(EntityAttributes.FLYING_SPEED) : (float) getAttributeValue(EntityAttributes.MOVEMENT_SPEED);
        setMovementSpeed(speed * getSpeedModifier());

         if (!getWorld().isClient() && (isOnGround() && !isInsideWaterOrBubbleColumn() || hasVehicle())) setFlying(false);
        setNoGravity(isFlying());

        if (isFlying()) {
            setPitch( MathHelper.clamp(getPitch(), -getPitchLimit(), getPitchLimit()));
            if (getInAirTimer() < maxInAirTimer) setInAirTimer(getInAirTimer() + 1);
            super.travel(movementInput);
        } else {
            setInAirTimer(0);
            super.travel(movementInput);
        }
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {return false;}

    protected float getOffGroundSpeed() {
        return getMovementSpeed() *  0.14f;
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
