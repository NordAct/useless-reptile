package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class LandDragonMoveControl <T extends URDragonEntity> extends MoveControl {
    private final T entity;

    public LandDragonMoveControl(T entity) {
        super(entity);
        this.entity = entity;
    }

    public void moveBack() {
        state = State.STRAFE;
    }

    public void notMove() {
        state = State.WAIT;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle()) return;

        double diffX = targetX - entity.getX();
        double diffY = targetY - entity.getY();
        double diffZ = targetZ - entity.getZ();
        double distanceSquared = diffX * diffX + diffY * diffY + diffZ * diffZ;
        float destinationYaw;
        float destinationPitch;
        if (entity.getTarget() != null && entity instanceof ShooterDragon shooterDragon) {
            Entity target = entity.getTarget();
            double diffTargetX = target.getX() - shooterDragon.getShootingPoint().pos().x;
            double diffTargetY = target.getY() - shooterDragon.getShootingPoint().pos().y;
            double diffTargetZ = target.getZ() - shooterDragon.getShootingPoint().pos().z;
            double distanceTargetXZ = Math.sqrt(diffTargetX * diffTargetX + diffTargetZ * diffTargetZ);
            destinationPitch = wrapDegrees(
                    entity.getPitch(),
                    (float)(-(MathHelper.atan2(diffTargetY, distanceTargetXZ) * MathHelper.DEGREES_PER_RADIAN)),
                    entity.getPitchLimit()
            );
            destinationYaw = (float)(MathHelper.atan2(diffTargetZ, diffTargetX) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
        } else {
            destinationYaw = (float)(MathHelper.atan2(diffZ, diffX) * MathHelper.DEGREES_PER_RADIAN) - 90.0F;
            destinationPitch = entity.getPitch();
        }
        entity.setMovingBackwards(false);
        float speed = getMovementSpeed();
        entity.setRotation(destinationYaw, destinationPitch);

        switch (state) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                state = State.WAIT;
                entity.setMovingBackwards(true);
                entity.setMovementSpeed(-speed);
            }
            case MOVE_TO -> {
                state = State.WAIT;
                if (distanceSquared < 2.500000277905201E-7D) {
                    entity.setUpwardSpeed(0.0F);
                    entity.setForwardSpeed(0.0F);
                    return;
                }
                if (entity.getLookControl().isLookingAtTarget() || entity.isLookingAtDirection(entity.getPitch(), destinationYaw, entity.getPitchLimit(), Math.max(50, entity.getRotationSpeed() * 2))) {
                    entity.setMovementSpeed(speed);
                } else entity.setForwardSpeed(0.0F);
            }
            case JUMPING -> {
                entity.setMovementSpeed(speed);
                if (entity.isOnGround()) state = State.WAIT;
            }
            default -> {
                entity.setUpwardSpeed(0.0F);
                entity.setForwardSpeed(0.0F);
                entity.setMovingBackwards(entity.isMoving());
            }
        }
    }

    private float getMovementSpeed() {
        return (float) entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }
}