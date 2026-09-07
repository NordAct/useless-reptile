package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public abstract class FlyingDragonBaseNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingPathNavigation {
    protected final T entity;

    public FlyingDragonBaseNavigation(T entity, Level world) {
        super(entity, world);
        this.entity = entity;
    }

    @Override
    public void tick() {
        checkFlight(entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() && !entity.hasTargetInWater() || entity.isInLava());
        entity.setPathfindingMalus(PathType.WATER, !entity.hasTargetInWater() ? 8 : 0);
        entity.setPathfindingMalus(PathType.WATER_BORDER, !entity.hasTargetInWater() ? 8 : 0);

        tick++;
    }

    @Override
    protected void followThePath() {
        if (isDone()) return;

        Vec3 vec3d = getTempMobPos();
        Vec3 currentTarget = Vec3.atBottomCenterOf(path.getNextNodePos());
        getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(entity.getX() - currentTarget.x());
        double yDiff = currentTarget.y() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.z());

        boolean bl = !entity.isFlying() && xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= entity.maxUpStep() && yDiff > -10.0D;

        if (bl || (entity.isFlying() || mob.level().noCollision(mob.getBoundingBox().inflate(0.5f, 0, 0.5f))) && canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

                float destinationYaw = (float) (Mth.atan2(currentTarget.x - entity.getX(), currentTarget.z - entity.getZ()) * Mth.RAD_TO_DEG) - 90.0F;
                boolean isRotatedTowards = (entity.getTarget() != null && entity.hasLineOfSight(entity.getTarget()))
                        || entity.isRotatedTowardsDirection(entity.getXRot(), destinationYaw, 90, entity.getHeadRotSpeed() * 2);

                if (!isRotatedTowards)
                    entity.getLookControl().setLookAt(currentTarget.x, entity.isFlying() ? currentTarget.y : currentTarget.y + entity.getEyeHeight(), currentTarget.z);
            }
            lastStuckCheck = tick;
        }

        if (currentTarget.distanceTo(getTargetPos().getCenter()) > entity.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected void checkFlight(boolean shouldFly) {
        if (!entity.isFlying() && shouldFly){
            entity.push(0, 0.1, 0);
            entity.startToFly();
        }
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        maxDistanceToWaypoint = Math.clamp(entity.getBbWidth() / 2, 0, 1) ;
        if (distance <= maxDistanceToWaypoint) stop();
    }

    protected FlyingDragonMoveControl<T> getMoveControl() {
        return (FlyingDragonMoveControl<T>) entity.getMoveControl();
    }
}

