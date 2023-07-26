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
import nordmods.uselessreptile.common.entity.ai.pathfinding.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.ai.pathfinding.FlyingDragonNavigation;
import nordmods.uselessreptile.common.network.LiftoffParticlesS2CPacket;

public abstract class URRideableFlyingDragonEntity extends URRideableDragonEntity implements FlyingDragon {
    protected final int maxInAirTimer = 600;
    protected float pitchLimitAir = 90;
    protected float rotationSpeedAir = 180;
    private int pressedTimer;

    protected URRideableFlyingDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        moveControl = new FlyingDragonMoveControl<>(this);
        navigation = new FlyingDragonNavigation<>(this, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(GLIDING, false);
        dataTracker.startTracking(FLYING, false);
        dataTracker.startTracking(TILT_STATE, (byte)0);//1 - вверх, 2 - вниз, 0 - летит прямо
        dataTracker.startTracking(IN_AIR_TIMER, 0);
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
    public void travel(Vec3d movementInput) {
        if (!isAlive()) return;

        if ((!isMoving() || isFlying())) setSprinting(false);
        if (isSprinting()) setSpeedMod(1.5f);
        else if (isMovingBackwards() && isFlying()) setSpeedMod(0.6f);
        else setSpeedMod(1f);
        float speed = isFlying() ? (float) getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED) : (float) getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        setMovementSpeed(speed * getSpeedMod());

        if (isOnGround()) setFlying(false);
        setNoGravity(isFlying());

        if (canBeControlledByRider()) {
            LivingEntity rider = getControllingPassenger();
            if (rider instanceof PlayerEntity player) super.travel(getControlledMovementInput(player, player.getVelocity()));
        } else {
            byte turnState = 0;
            float rotationSpeed = getRotationSpeed();
            float yawDiff = bodyYaw - headYaw;
            turnState = (Math.abs(yawDiff)) > rotationSpeed && yawDiff < 0 ? 2 : turnState;
            turnState = (Math.abs(yawDiff)) > rotationSpeed && yawDiff > 0 ? 1 : turnState;
            setTurningState(turnState);

            if (isFlying()) {
                setPitch( MathHelper.clamp(getPitch(), -getPitchLimit(), getPitchLimit()));
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

        float f1 = MathHelper.clamp(rider.forwardSpeed, -forwardSpeed, forwardSpeed);
        setMovingBackwards(isMoveBackPressed() || (!isMoveForwardPressed() && !isMoveBackPressed() && isMoving()));
        setPitch(MathHelper.clamp(rider.getPitch(), -getPitchLimit(), getPitchLimit()));
        if (!isFlying()) {

            if (isSprintPressed()) setSprinting(true);
            if (isMovingBackwards() && (isMoveBackPressed() || isMoveBackPressed())) setSprinting(false);
            setRotation(rider);
            if (isJumpPressed()) pressedTimer++;
            if (!isJumpPressed() && pressedTimer <= 5 && pressedTimer != 0) {
                if (isOnGround()) jump();
                pressedTimer = 0;
            }
            if (isJumpPressed() && pressedTimer > 5) {
                startToFly();
                pressedTimer = 0;
            }

            return new Vec3d(0, movementInput.y, f1);
        } else {
            float pitchSpeed = 2;
            setRotation(rider);
            float verticalSpeed = 0F;
            setGliding(accelerationModifier > 1);

            if (isJumpPressed()) {
                verticalSpeed = 0.3F;
                setTiltState((byte) 1);
                setGliding(false);
                if (!isMovingBackwards() && isMoving() && getPitch() > -getPitchLimit() && !isDownPressed())
                    setPitch(getPitch() - pitchSpeed);
            }
            if (isDownPressed()) {
                verticalSpeed = -0.4F;
                setTiltState((byte) 2);
                if (!isMovingBackwards() && isMoving() && getPitch() < getPitchLimit())
                    setPitch(getPitch() + pitchSpeed);
            }
            float currentVerticalSpeed = (float) getVelocity().getY();
            if (!(isJumpPressed() || isDownPressed())) {
                if (getPitch() != 0) {
                    if (getPitch() < 0 && getPitch() < -pitchSpeed)
                        setPitch(getPitch() + pitchSpeed);
                    if (getPitch() > 0 && getPitch() > pitchSpeed)
                        setPitch(getPitch() - pitchSpeed);
                    if (getPitch() < pitchSpeed && getPitch() > -pitchSpeed) setPitch(0);
                }
                if (currentVerticalSpeed != 0) verticalSpeed = currentVerticalSpeed * -0.5F;
                setTiltState((byte) 0);
            }

            return new Vec3d(0, verticalSpeed * MathHelper.clamp(accelerationModifier, 0.25, 1.5), f1 * accelerationModifier * 2.5F);
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
        if (isFlying()) return rotationSpeedAir * calcSpeedMod();
        return rotationSpeedGround * calcSpeedMod();
    }

    @Override
    public float getPitchLimit() {
        if (isFlying() && isMoving() && !isMovingBackwards()) return pitchLimitAir;
        return pitchLimitGround;
    }

    @Override
    protected float calcSpeedMod() {
        double baseSpeed = isFlying() ? getAttributeBaseValue(EntityAttributes.GENERIC_FLYING_SPEED) : getAttributeBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        return (float) (getMovementSpeed() / baseSpeed);
    }

    public int getMaxInAirTimer() {
        return maxInAirTimer;
    }

    protected float getOffGroundSpeed() {
        float movementSpeed = getMovementSpeed();
        return hasControllingPassenger() ? movementSpeed * 0.1f : movementSpeed *  0.14f;
    }
}
