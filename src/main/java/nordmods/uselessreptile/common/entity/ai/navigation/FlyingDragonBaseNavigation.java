package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public abstract class FlyingDragonBaseNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingPathNavigation implements DragonNavigation {
    protected final T mob;

    public FlyingDragonBaseNavigation(T mob, Level world) {
        super(mob, world);
        this.mob = mob;
    }

    @Override
    public void tick() {
        checkFlight(mob.isInWater() && mob.getFluidHeight(FluidTags.WATER) > mob.getFluidJumpThreshold() && !mob.hasTargetInWater() || mob.isInLava());
        mob.setPathfindingMalus(PathType.WATER, !mob.hasTargetInWater() ? 8 : 0);
        mob.setPathfindingMalus(PathType.WATER_BORDER, !mob.hasTargetInWater() ? 8 : 0);

        tick++;

        if (hasDelayedRecomputation) recomputePath();
    }

    @Override
    protected void followThePath() {
        if (isDone()) return;

        Vec3 vec3d = getTempMobPos();
        Vec3 currentTarget = Vec3.atBottomCenterOf(path.getNextNodePos());
        getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(mob.getX() - currentTarget.x());
        double yDiff = currentTarget.y() - mob.getY();
        double zDiff = Math.abs(mob.getZ() - currentTarget.z());

        boolean bl = !mob.isFlying() && xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= mob.maxUpStep() && yDiff > -10.0D;

        if (bl || (mob.isFlying() || mob.level().noCollision(mob.getBoundingBox().inflate(0.5f, 0, 0.5f))) && canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);
            }
            lastStuckCheck = tick;
        }

        if (currentTarget.distanceTo(getTargetPos().getCenter()) > mob.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected void checkFlight(boolean shouldFly) {
        if (!mob.isFlying() && shouldFly){
            mob.forceFlightNextTick();
        }
    }

    protected void moveOrStop(BlockPos target) {
        double distance = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        maxDistanceToWaypoint = Math.clamp(mob.getBbWidth() / 2, 0, 1) ;
        if (distance <= maxDistanceToWaypoint) stop();
    }

    protected FlyingDragonMoveControl<T> getMoveControl() {
        return (FlyingDragonMoveControl<T>) mob.getMoveControl();
    }

    @Override
    public void scheduleRecomputation() {
        hasDelayedRecomputation = true;
    }
}

