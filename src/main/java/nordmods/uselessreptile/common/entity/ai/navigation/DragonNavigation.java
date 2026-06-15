package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonNavigation extends GroundPathNavigation {
    protected final URDragonEntity entity;
    protected boolean nodeChecked;
    protected boolean isSurroundingEmpty;

    public DragonNavigation(URDragonEntity mobEntity, Level world) {
        super(mobEntity, world);
        this.entity = mobEntity;
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
        Vec3 vec3d = getTempMobPos();
        Vec3 currentTarget = Vec3.atBottomCenterOf(path.getNextNodePos());
        getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(entity.getX() - currentTarget.x());
        double yDiff = currentTarget.y() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.z());

        boolean bl = xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= entity.maxUpStep() && yDiff > -entity.getMaxFallDistance();

        if (bl || canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);
            }
            lastStuckCheck = tick;
            nodeChecked = false;
        }

        if (currentTarget.distanceTo(Vec3.atCenterOf(getTargetPos())) > entity.position().distanceTo(Vec3.atCenterOf(getTargetPos()))) recomputePath();
    }

    protected boolean shouldTargetNextNodeInDirection(Vec3 currentPos) {
        if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) return false;
        if (!entity.horizontalCollision && canMoveDirectly(currentPos, path.getNextEntityPos(entity))) return true;
        BlockPos currentNode = path.getNextNodePos();

        if (!nodeChecked) {
            isSurroundingEmpty = true;
            BlockPos[] toCheck = new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north()};

            for (BlockPos pos : toCheck) {
                if (entity.getPathfindingMalus(nodeEvaluator.getTarget(pos.getX(), pos.getY(), pos.getZ()).type) == 0) continue;
                isSurroundingEmpty = false;
                break;
            }
            nodeChecked = true;
        }

        return currentPos.closerThan(new Vec3(currentNode.getX() + 0.5, isSurroundingEmpty ? entity.getY() + 0.5 : currentNode.getY(), currentNode.getZ() + 0.5), maxDistanceToWaypoint);
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
