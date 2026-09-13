package nordmods.uselessreptile.common.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
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
        double distanceXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float destinationYaw = (float) (Mth.atan2(diffZ, diffX) * Mth.RAD_TO_DEG) - 90.0F;

        boolean navigationDone = entity.getNavigation().isDone();
        boolean isRotatedTowards = entity.getNavigation().isDone()
                || (entity.getTarget() != null && entity.hasLineOfSight(entity.getTarget()))
                || entity.isRotatedTowardsDirection(entity.getXRot(), destinationYaw, 90, entity.getHeadRotSpeed() * 2);

        float accelerationModifier = Math.max(entity.getAccelerationModifier() , 0.25f);
        entity.setMovingBackwards(false);
        float speed = getMovementSpeed(accelerationModifier);

        if (!isRotatedTowards) {
            entity.getBodyRotationControl().setRotationTarget(wantedX, wantedZ);
            operation = Operation.WAIT;
        }

        switch (operation) {
            case STRAFE -> { //there's no strafe for dragons, but it's used for backwards movement
                operation = Operation.WAIT;
                entity.setMovingBackwards(true);
                entity.setSpeed(-speed);
            }

            case MOVE_TO -> {
                operation = Operation.WAIT;
                entity.setSpeed(speed);
                if (distanceSquared < 2.500000277905201E-7D) {
                    entity.setYya(0.0F);
                    entity.setZza(0.0F);
                    return;
                }

                if (!navigationDone) {
                    BlockPos pos = this.mob.blockPosition();
                    BlockState blockState = this.mob.level().getBlockState(pos);
                    VoxelShape shape = blockState.getCollisionShape(this.mob.level(), pos);
                    if (diffY > this.mob.maxUpStep() && distanceXZ < Math.max(1.0F, this.mob.getBbWidth())
                            || !shape.isEmpty() && this.mob.getY() < shape.max(Direction.Axis.Y) + pos.getY() && !blockState.is(BlockTags.DOORS) && !blockState.is(BlockTags.FENCES)
                            || entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() && !entity.hasTargetInWater() || entity.isInLava()) {
                        entity.getJumpControl().jump();
                        this.operation = MoveControl.Operation.JUMPING;
                    }
                }
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

    private float getMovementSpeed(float accelerationModifier) {
        float speed = (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        return (float) (speed * speedModifier * accelerationModifier);
    }
}
