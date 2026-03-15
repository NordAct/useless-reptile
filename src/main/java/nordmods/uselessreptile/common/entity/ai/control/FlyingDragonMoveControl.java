package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonMoveControl<T extends URDragonEntity & FlyingDragon> extends MoveControl {
    private final T entity;
    private boolean forceFlyUp = false;
    private boolean forceFlyDown = false;

    public FlyingDragonMoveControl(T entity) {
        super(entity);
        this.entity = entity;
    }

    public void moveBack() {
        operation = Operation.STRAFE;
    }

    public void notMove() {
        operation = Operation.WAIT;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.isPassenger()) return;

        double diffX = wantedX - entity.getX();
        double diffY = wantedY - entity.getY();
        double diffZ = wantedZ - entity.getZ();
        double distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
        double distanceXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float destinationYaw;
        float destinationPitch;
        if (!entity.isOrderedToSit()) {
            if (entity.getTarget() != null && entity instanceof ShooterDragon shooterDragon) {
                Entity target = entity.getTarget();
                double diffTargetX = target.getX() - shooterDragon.getShootingPoint().position().x;
                double diffTargetY = target.getY() - shooterDragon.getShootingPoint().position().y;
                double diffTargetZ = target.getZ() - shooterDragon.getShootingPoint().position().z;
                double distanceTargetXZ = Math.sqrt(diffTargetX * diffTargetX + diffTargetZ * diffTargetZ);
                destinationPitch = rotlerp(
                        entity.getXRot(),
                        (float) (-(Mth.atan2(diffTargetY, distanceTargetXZ) * Mth.RAD_TO_DEG)),
                        entity.getPitchLimit()
                );
                destinationYaw = (float) (Mth.atan2(diffTargetZ, diffTargetX) * Mth.RAD_TO_DEG) - 90.0F;
            } else {
                destinationYaw = (float) (Mth.atan2(diffZ, diffX) * Mth.RAD_TO_DEG) - 90.0F;
                destinationPitch = entity.getXRot();
            }
        } else {
            destinationYaw = entity.getYRot();
            destinationPitch = entity.getXRot();
        }

        boolean inWater = entity.isInWater() && entity.isAffectedByFluids();

        if (Double.isNaN(entity.getDeltaMovement().y)) entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);
        int accelerationDuration = entity.getAccelerationDuration();
        if (accelerationDuration < 0) accelerationDuration = 0;
        float accelerationModifier = (float)accelerationDuration/entity.getMaxAccelerationDuration();
        if (accelerationModifier > 1.5) accelerationModifier = 1.5f;
        entity.setMovingBackwards(false);
        entity.setTiltState((byte) 0);
        float verticalAccelerationModifier = Mth.clamp(accelerationModifier, 0.25f, 1.5f);
        float speed = getMovementSpeed(accelerationModifier, inWater);

        entity.setRot(destinationYaw, destinationPitch);

        switch (operation) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                operation = Operation.WAIT;
                entity.setMovingBackwards(true);

                if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.25) accelerationDuration -= 2;
                else accelerationDuration++;

                entity.setSpeed(-speed);
            }
            case MOVE_TO -> {
                operation = Operation.WAIT;
                if (distanceSquared < 2.500000277905201E-7D) {
                    entity.setYya(0.0F);
                    entity.setZza(0.0F);
                    return;
                }
                if (entity.getLookControl().isLookingAtTarget() || entity.isLookingAtDirection(entity.getXRot(), destinationYaw, entity.getPitchLimit(), Math.max(50, entity.getRotationSpeed() * 2))) {
                    if (accelerationDuration < entity.getMaxAccelerationDuration()) accelerationDuration++;
                    if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration--;

                    entity.setSpeed(speed);
                } else entity.setZza(0.0F);
            }
            case JUMPING -> {
                entity.setSpeed(speed);
                if (entity.onGround()) operation = Operation.WAIT;
            }
            default -> {
                entity.setYya(0.0F);
                entity.setZza(0.0F);
                entity.setMovingBackwards(entity.isMoving());
                accelerationDuration /= 2;
                if (!entity.isMoving()) accelerationDuration = 0;
            }
        }

        if (entity.isFlying()) {
            if (isFlyDirectionEnforced()) {
                if (forceFlyUp) accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
                if (forceFlyDown) accelerationDuration = flyDown(accelerationDuration, verticalAccelerationModifier);
            } else if (Math.abs(diffY) > 9.999999747378752E-6D || Math.abs(distanceXZ) > 9.999999747378752E-6D) {
                destinationPitch = (float)(-(Mth.atan2(diffY, distanceXZ) * 57.2957763671875D));
                entity.setXRot(rotlerp(entity.getXRot(), destinationPitch, entity.getPitchLimit()));
                entity.setYya(0);

                if (!entity.isInWater() || entity.hasTargetInWater()) {
                    double divergence = Math.clamp(Math.max(0, (distanceXZ - (entity.getWidthMod() < 2 ? 0 : 4)) * 0.5), 0, 3);
                    if (diffY > divergence) accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
                    if (diffY < -divergence) accelerationDuration = flyDown(accelerationDuration, verticalAccelerationModifier);
                } else accelerationDuration = flyUp(accelerationDuration, verticalAccelerationModifier);
            }
        }
        entity.setAccelerationDuration(accelerationDuration);
        forceFlyUp = false;
        forceFlyDown = false;
    }

    public void forceFlyUp() {
         forceFlyUp = true;
    }

    public void forceFlyDown() {
        forceFlyDown = true;
    }

    private int flyUp (int accelerationDuration, float verticalAccelerationModifier) {
        if (accelerationDuration > entity.getMaxAccelerationDuration() * 0.4) accelerationDuration -= 2;
        if (accelerationDuration > entity.getMaxAccelerationDuration()) accelerationDuration -= 2;
        entity.setYya(entity.getVerticalSpeed() * verticalAccelerationModifier);
        entity.setTiltState((byte) 1);
        return accelerationDuration;
    }

    private int flyDown (int accelerationDuration, float verticalAccelerationModifier) {
        if (accelerationDuration < entity.getMaxAccelerationDuration() * 3) accelerationDuration += 2;
        entity.setYya(-entity.getVerticalSpeed() * verticalAccelerationModifier * 1.3f);
        entity.setTiltState((byte) 2);
        return accelerationDuration;
    }

    private boolean isFlyDirectionEnforced() {
        return forceFlyDown || forceFlyUp;
    }

    private float getMovementSpeed(float accelerationModifier, boolean inWater) {
        float speed;
        if (entity.isFlying()) {
            speed = (float) entity.getAttributeValue(Attributes.FLYING_SPEED) * accelerationModifier;
            if (inWater || entity.getLastDamageSource() == entity.damageSources().lava()) entity.getJumpControl().jump();
        } else speed = (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        return speed;
    }
}
