package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class LandDragonMoveControl <T extends URDragonEntity> extends MoveControl {
    private final T entity;

    public LandDragonMoveControl(T entity) {
        super(entity);
        this.entity = entity;
    }

    public void moveBack() {
        state = MoveControl.State.STRAFE;
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
        float destinationYaw = (float)(MathHelper.atan2(diffZ, diffX) * 57.2957763671875D) - 90.0F;
        entity.setMovingBackwards(false);

        float speed = getMovementSpeed();

        switch (state) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                state = State.WAIT;
                entity.setMovingBackwards(true);

                entity.setRotation(destinationYaw, entity.getPitch());
                entity.setMovementSpeed(-speed * entity.getSpeedModifier() * 0.5f);
            }
            case MOVE_TO -> {
                state = State.WAIT;
                if (distanceSquared < 2.500000277905201E-7D) {
                    entity.setUpwardSpeed(0.0F);
                    entity.setForwardSpeed(0.0F);
                    return;
                }

                entity.setRotation(destinationYaw, entity.getPitch());
                entity.setMovementSpeed(speed * entity.getSpeedModifier());
            }
            case JUMPING -> {
                entity.setMovementSpeed(speed * entity.getSpeedModifier());
                if (entity.isOnGround()) state = MoveControl.State.WAIT;
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
