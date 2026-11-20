package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

public class LandDragonNavigation<T extends  URDragonEntity> extends GroundPathNavigation implements DragonNavigation {
    protected final T mob;

    public LandDragonNavigation(T mob, Level world) {
        super(mob, world);
        this.mob = mob;
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new DragonWalkNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (mob.hasControllingPassenger() || mob.isPassenger() || mob.isOrderedToSit()) return;

        boolean isFullBlock = mob.getBlockStateOn().isCollisionShapeFullBlock(mob.level(), mob.getOnPos());
        if (GoalUtils.isSolid(mob, mob.blockPosition()) && isFullBlock) mob.getJumpControl().jump();
        mob.setPathfindingMalus(PathType.WATER, !mob.hasTargetInWater() ? 8 : 0);
        mob.setPathfindingMalus(PathType.WATER_BORDER, !mob.hasTargetInWater() ? 8 : 0);

        tick++;

        if (hasDelayedRecomputation) recomputePath();

        BlockPos target = getTargetPos();
        if (!isDone() && target != null) {
            followThePath();
            moveOrStop(target);
        }
        doStuckDetection(getTempMobPos());
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

        boolean bl = xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= mob.maxUpStep() && yDiff > -10.0D;

        if (bl || mob.level().noCollision(mob.getBoundingBox().inflate(0.5f, 0, 0.5f)) && canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

                float destinationYaw = (float) (Mth.atan2(currentTarget.x - mob.getX(), currentTarget.z - mob.getZ()) * Mth.RAD_TO_DEG) - 90.0F;
                boolean isRotatedTowards = (mob.getTarget() != null && mob.hasLineOfSight(mob.getTarget()))
                        || mob.isRotatedTowardsDirection(mob.getXRot(), destinationYaw, 90, mob.getHeadRotSpeed() * 2);

                if (!isRotatedTowards)
                    mob.getLookControl().setLookAt(currentTarget.x, currentTarget.y + mob.getEyeHeight(), currentTarget.z);
            }
            lastStuckCheck = tick;
        }

        if (currentTarget.distanceTo(getTargetPos().getCenter()) > mob.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected void moveOrStop(BlockPos target) {
        double distance = mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
        maxDistanceToWaypoint = mob.getBbWidth() / 2;
        if (distance <= maxDistanceToWaypoint) stop();
    }

    protected MoveControl getMoveControl() {
        return mob.getMoveControl();
    }

    @Override
    public void scheduleRecomputation() {
        hasDelayedRecomputation = true;
    }
}
