package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public abstract class FlyingDragonBaseNavigation<T extends URDragonEntity & FlyingDragon> extends FlyingPathNavigation {
    protected final T entity;
    protected int jumpCount;
    protected boolean nodeChecked;
    protected boolean isSurroundingEmpty;

    public FlyingDragonBaseNavigation(T entity, Level world) {
        super(entity, world);
        this.entity = entity;
    }

    @Override
    public void tick() {
        boolean isFullBlock = entity.getBlockStateOn().isCollisionShapeFullBlock(entity.level(), entity.getOnPos());
        if (GoalUtils.isSolid(entity, entity.blockPosition()) && isFullBlock) entity.getJumpControl().jump();
        if (entity.isInWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getFluidJumpThreshold() && !entity.hasTargetInWater() || entity.isInLava())
            startToFly(jumpCount > 9 || entity.isInLava());
        entity.setPathfindingMalus(PathType.WATER, !entity.hasTargetInWater() ? 8 : 0);
        entity.setPathfindingMalus(PathType.WATER_BORDER, !entity.hasTargetInWater() ? 8 : 0);

        tick++;
    }

    @Override
    protected void followThePath() {
        Vec3 vec3d = getTempMobPos();
        Vec3 currentTarget = Vec3.atBottomCenterOf(path.getNextNodePos());
        getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(entity.getX() - currentTarget.x());
        double yDiff = currentTarget.y() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.z());

        boolean bl = !entity.isFlying() && xDiff < (double)maxDistanceToWaypoint && zDiff < (double)maxDistanceToWaypoint &&  yDiff <= entity.maxUpStep() && yDiff > -10.0D;

        if (bl || canCutCorner(path.getNextNode().type) && shouldTargetNextNodeInDirection(vec3d)) {
            path.advance();
            if (!path.isDone()) {
                currentTarget = Vec3.atBottomCenterOf(getTargetPos());
                getMoveControl().setWantedPosition(currentTarget.x, currentTarget.y, currentTarget.z, 1);
            }
            jumpCount = 0;
            lastStuckCheck = tick;
            nodeChecked = false;
        }

        if (currentTarget.distanceTo(getTargetPos().getCenter()) > entity.position().distanceTo(getTargetPos().getCenter())) recomputePath();
    }

    protected boolean shouldTargetNextNodeInDirection(Vec3 currentPos) {
        if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) return false;
        if (!entity.horizontalCollision && canMoveDirectly(currentPos, path.getNextEntityPos(entity))) return true;
        BlockPos currentNode = path.getNextNodePos();

        if (!nodeChecked) {
            isSurroundingEmpty = true;
            BlockPos[] toCheck = entity.isFlying() ?
                    new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north(), currentNode.above(), currentNode.below()} :
                    new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north()};


            for (BlockPos pos : toCheck) {
                if (entity.getPathfindingMalus(nodeEvaluator.getTarget(pos.getX(), pos.getY(), pos.getZ()).type) == 0) continue;
                isSurroundingEmpty = false;
                break;
            }
            nodeChecked = true;
        }

        return currentPos.closerThan(new Vec3(currentNode.getX() + 0.5, isSurroundingEmpty ? entity.getY() + 0.5 : currentNode.getY(), currentNode.getZ() + 0.5), maxDistanceToWaypoint);
    }

    protected void startToFly(boolean shouldFly) {
        if (shouldFly){
            entity.push(0, 0.1, 0);
            entity.startToFly();
            jumpCount = 0;
        } else jumpCount++;
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        maxDistanceToWaypoint = entity.getBbWidth() / 2;
        if (!entity.isFlying()) maxDistanceToWaypoint = Math.clamp(maxDistanceToWaypoint, 0, .5f) ;
        if (distance <= maxDistanceToWaypoint) stop();
    }

    protected FlyingDragonMoveControl<T> getMoveControl() {
        return (FlyingDragonMoveControl<T>) entity.getMoveControl();
    }
}

