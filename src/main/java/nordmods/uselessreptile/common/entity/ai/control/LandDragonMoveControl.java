package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class LandDragonMoveControl <T extends URDragonEntity> extends MoveControl {
    private final T entity;

    public LandDragonMoveControl(T entity) {
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
        float destinationYaw;
        float destinationPitch;
        if (entity.getTarget() != null && entity instanceof ShooterDragon shooterDragon) {
            Entity target = entity.getTarget();
            double diffTargetX = target.getX() - shooterDragon.getShootingPoint().pos().x;
            double diffTargetY = target.getY() - shooterDragon.getShootingPoint().pos().y;
            double diffTargetZ = target.getZ() - shooterDragon.getShootingPoint().pos().z;
            double distanceTargetXZ = Math.sqrt(diffTargetX * diffTargetX + diffTargetZ * diffTargetZ);
            destinationPitch = rotlerp(
                    entity.getXRot(),
                    (float)(-(Mth.atan2(diffTargetY, distanceTargetXZ) * Mth.RAD_TO_DEG)),
                    entity.getPitchLimit()
            );
            destinationYaw = (float)(Mth.atan2(diffTargetZ, diffTargetX) * Mth.RAD_TO_DEG) - 90.0F;
        } else {
            destinationYaw = (float)(Mth.atan2(diffZ, diffX) * Mth.RAD_TO_DEG) - 90.0F;
            destinationPitch = entity.getXRot();
        }
        entity.setMovingBackwards(false);
        float speed = getMovementSpeed();
        entity.setRot(destinationYaw, destinationPitch);

        switch (operation) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                operation = Operation.WAIT;
                entity.setMovingBackwards(true);
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
            }
        }
    }

    private float getMovementSpeed() {
        return (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }
}
