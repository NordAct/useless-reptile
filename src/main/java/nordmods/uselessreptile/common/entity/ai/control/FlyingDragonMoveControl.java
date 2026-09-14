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
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
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
        float destinationYaw = (float) (Mth.atan2(diffZ, diffX) * Mth.RAD_TO_DEG) - 90.0F;

        boolean navigationDone = entity.getNavigation().isDone();
        boolean isRotatedTowards = entity.getNavigation().isDone()
                || (entity.getTarget() != null && entity.hasLineOfSight(entity.getTarget()) && entity.getLookControl().isLookingAtTarget())
                || entity.isRotatedTowardsDirection(entity.getXRot(), destinationYaw, 90, entity.getHeadRotSpeed() * 2);

        if (Double.isNaN(entity.getDeltaMovement().y)) entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);
        float accelerationModifier = Math.max(entity.getAccelerationModifier() , 0.25f);
        entity.setMovingBackwards(false);
        float verticalAccelerationModifier = Mth.clamp(accelerationModifier, 0.25f, 1.5f);
        float speed = getMovementSpeed(accelerationModifier);

        if (!isRotatedTowards) {
            entity.getBodyRotationControl().setRotationTarget(wantedX, wantedZ);
            if (!entity.isFlying()) operation = Operation.WAIT;
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

                if (!entity.isFlying() && !navigationDone) {
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
                if (entity.onGround() || entity.isFlying()) operation = Operation.WAIT;
            }
            default -> {
                entity.setYya(0.0F);
                entity.setZza(0.0F);
                entity.setMovingBackwards(entity.isMoving());
            }
        }

        if (entity.isFlying()) {
            if (isFlyDirectionEnforced()) {
                if (forceFlyUp) flyUp(verticalAccelerationModifier);
                if (forceFlyDown) flyDown(verticalAccelerationModifier);
            } else if (!navigationDone && (Math.abs(diffY) > 9.999999747378752E-6D || Math.abs(distanceXZ) > 9.999999747378752E-6D)) {
                entity.setYya(0);

                if ((!entity.isInWater() || entity.hasTargetInWater())) {
                    double divergence = Math.clamp(Math.max(0, (distanceXZ - (entity.getBbWidth() < 2 ? 0 : 4)) * 0.5), 0, 3);
                    if (diffY > divergence) flyUp(verticalAccelerationModifier);
                    if (diffY < -divergence) flyDown(verticalAccelerationModifier);
                } else flyUp(verticalAccelerationModifier);
            }
        }
        forceFlyUp = false;
        forceFlyDown = false;
    }

    public void forceFlyUp() {
         forceFlyUp = true;
    }

    public void forceFlyDown() {
        forceFlyDown = true;
    }

    private void flyUp (float verticalAccelerationModifier) {
        entity.setYya(entity.getVerticalSpeed() * verticalAccelerationModifier);
    }

    private void flyDown (float verticalAccelerationModifier) {
        entity.setYya(-entity.getVerticalSpeed() * verticalAccelerationModifier);
    }

    private boolean isFlyDirectionEnforced() {
        return forceFlyDown || forceFlyUp;
    }

    private float getMovementSpeed(float accelerationModifier) {
        float speed;
        if (entity.isFlying()) {
            speed = (float) entity.getAttributeValue(Attributes.FLYING_SPEED);
        } else speed = (float) entity.getAttributeValue(Attributes.MOVEMENT_SPEED);
        return (float) (speed * speedModifier * accelerationModifier);
    }
}
