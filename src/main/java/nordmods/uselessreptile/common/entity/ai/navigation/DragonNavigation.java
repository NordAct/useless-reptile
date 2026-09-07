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

public class DragonNavigation extends GroundPathNavigation {
    protected final URDragonEntity entity;
    protected boolean nodeChecked;

    public DragonNavigation(URDragonEntity mobEntity, Level world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    protected @NonNull PathFinder createPathFinder(int range) {
        nodeEvaluator = new DragonWalkNodeEvaluator();
        return new PathFinder(nodeEvaluator, range);
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.isPassenger() || entity.isOrderedToSit()) return;

        boolean isFullBlock = entity.getBlockStateOn().isCollisionShapeFullBlock(entity.level(), entity.getOnPos());
        if (GoalUtils.isSolid(entity, entity.blockPosition()) && isFullBlock) entity.getJumpControl().jump();
        entity.setPathfindingMalus(PathType.WATER, !entity.hasTargetInWater() ? 8 : 0);
        entity.setPathfindingMalus(PathType.WATER_BORDER, !entity.hasTargetInWater() ? 8 : 0);

        tick++;

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

        double xDiff = Math.abs(entity.getX() - currentTarget.x());
        double yDiff = currentTarget.y() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.z());

        boolean bl = xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= entity.maxUpStep() && yDiff > -10.0D;

        if (bl || mob.level().noCollision(mob.getBoundingBox().inflate(0.5f, 0, 0.5f)) && canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

                float destinationYaw = (float) (Mth.atan2(currentTarget.x - entity.getX(), currentTarget.z - entity.getZ()) * Mth.RAD_TO_DEG) - 90.0F;
                boolean isRotatedTowards = (entity.getTarget() != null && entity.hasLineOfSight(entity.getTarget()))
                        || entity.isRotatedTowardsDirection(entity.getXRot(), destinationYaw, 90, entity.getHeadRotSpeed() * 2);

                if (!isRotatedTowards)
                    entity.getLookControl().setLookAt(currentTarget.x, currentTarget.y + entity.getEyeHeight(), currentTarget.z);
            }
            lastStuckCheck = tick;
        }

        if (currentTarget.distanceTo(getTargetPos().getCenter()) > entity.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        maxDistanceToWaypoint = entity.getBbWidth() / 2;
        if (distance <= maxDistanceToWaypoint) stop();
    }

    protected MoveControl getMoveControl() {
        return entity.getMoveControl();
    }
}
