package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonNavigation extends MobNavigation {
    protected final URDragonEntity entity;
    protected boolean nodeChecked;
    protected boolean isSurroundingEmpty;

    public DragonNavigation(URDragonEntity mobEntity, World world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle() || entity.isSitting()) return;

        boolean isFullBlock = entity.getSteppingBlockState().isFullCube(entity.getEntityWorld(), entity.getSteppingPos());
        if (NavigationConditions.isSolidAt(entity, entity.getBlockPos()) && isFullBlock) entity.getJumpControl().setActive();
        entity.setPathfindingPenalty(PathNodeType.WATER, !entity.hasTargetInWater() ? 8 : 0);
        entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, !entity.hasTargetInWater() ? 8 : 0);

        tickCount++;

        BlockPos target = getTargetPos();
        if (!isIdle() && target != null) {
            continueFollowingPath();
            moveOrStop(target);
        }
        checkTimeouts(getPos());
    }

    @Override
    protected void continueFollowingPath() {
        Vec3d vec3d = getPos();
        Vec3d currentTarget = Vec3d.ofBottomCenter(currentPath.getCurrentNodePos());
        getMoveControl().moveTo(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(entity.getX() - currentTarget.getX());
        double yDiff = currentTarget.getY() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.getZ());

        boolean bl = xDiff < (double)nodeReachProximity && zDiff < (double)nodeReachProximity &&  yDiff <= entity.getStepHeight() && yDiff > -entity.getSafeFallDistance();

        if (bl || canJumpToNext(currentPath.getCurrentNode().type) && shouldJumpToNextNode(vec3d)) {
            currentPath.next();
            if (!currentPath.isFinished()) {
                currentTarget = Vec3d.ofBottomCenter(getTargetPos());
                getMoveControl().moveTo(currentTarget.x, currentTarget.y, currentTarget.z, 1);
            }
            pathStartTime = tickCount;
            nodeChecked = false;
        }

        if (currentTarget.distanceTo(getTargetPos().toCenterPos()) > entity.getEntityPos().distanceTo(getTargetPos().toCenterPos())) recalculatePath();
    }

    protected boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (currentPath.getCurrentNodeIndex() + 1 >= currentPath.getLength()) return false;
        if (!entity.horizontalCollision && canPathDirectlyThrough(currentPos, currentPath.getNodePosition(entity))) return true;
        BlockPos currentNode = currentPath.getCurrentNodePos();

        if (!nodeChecked) {
            isSurroundingEmpty = true;
            BlockPos[] toCheck = new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north()};

            for (BlockPos pos : toCheck) {
                if (entity.getPathfindingPenalty(nodeMaker.getNode(pos.getX(), pos.getY(), pos.getZ()).type) == 0) continue;
                isSurroundingEmpty = false;
                break;
            }
            nodeChecked = true;
        }

        return currentPos.isInRange(new Vec3d(currentNode.getX() + 0.5, isSurroundingEmpty ? entity.getY() + 0.5 : currentNode.getY(), currentNode.getZ() + 0.5), nodeReachProximity);
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        nodeReachProximity = entity.getWidth() / 2;
        if (distance <= nodeReachProximity) stop();
    }

    protected MoveControl getMoveControl() {
        return entity.getMoveControl();
    }
}
