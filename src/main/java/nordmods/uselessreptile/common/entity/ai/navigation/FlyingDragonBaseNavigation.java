package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.ai.control.FlyingDragonMoveControl;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public abstract class FlyingDragonBaseNavigation<T extends URDragonEntity & FlyingDragon> extends BirdNavigation {
    protected final T entity;
    protected int jumpCount;
    protected boolean nodeChecked;
    protected boolean isSurroundingEmpty;

    public FlyingDragonBaseNavigation(T entity, World world) {
        super(entity, world);
        this.entity = entity;
    }

    @Override
    public void tick() {
        boolean isFullBlock = entity.getSteppingBlockState().isFullCube(entity.getEntityWorld(), entity.getSteppingPos());
        if (NavigationConditions.isSolidAt(entity, entity.getBlockPos()) && isFullBlock) entity.getJumpControl().setActive();
        if (entity.isTouchingWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getSwimHeight() && !entity.hasTargetInWater() || entity.isInLava())
            startToFly(jumpCount > 9 || entity.isInLava());
        entity.setPathfindingPenalty(PathNodeType.WATER, !entity.hasTargetInWater() ? 8 : 0);
        entity.setPathfindingPenalty(PathNodeType.WATER_BORDER, !entity.hasTargetInWater() ? 8 : 0);

        tickCount++;
    }

    @Override
    protected void continueFollowingPath() {
        Vec3d vec3d = getPos();
        Vec3d currentTarget = Vec3d.ofBottomCenter(currentPath.getCurrentNodePos());
        getMoveControl().moveTo(currentTarget.x, currentTarget.y, currentTarget.z, 1);

        double xDiff = Math.abs(entity.getX() - currentTarget.getX());
        double yDiff = currentTarget.getY() - entity.getY();
        double zDiff = Math.abs(entity.getZ() - currentTarget.getZ());

        boolean bl = !entity.isFlying() && xDiff < (double)nodeReachProximity && zDiff < (double)nodeReachProximity &&  yDiff <= entity.getStepHeight() && yDiff > -10.0D;

        if (bl || canJumpToNext(currentPath.getCurrentNode().type) && shouldJumpToNextNode(vec3d)) {
            currentPath.next();
            if (!currentPath.isFinished()) {
                currentTarget = Vec3d.ofBottomCenter(getTargetPos());
                getMoveControl().moveTo(currentTarget.x, currentTarget.y, currentTarget.z, 1);
            }
            jumpCount = 0;
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
            BlockPos[] toCheck = entity.isFlying() ?
                    new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north(), currentNode.up(), currentNode.down()} :
                    new BlockPos[]{currentNode.east(), currentNode.west(), currentNode.south(), currentNode.north()};


            for (BlockPos pos : toCheck) {
                if (entity.getPathfindingPenalty(nodeMaker.getNode(pos.getX(), pos.getY(), pos.getZ()).type) == 0) continue;
                isSurroundingEmpty = false;
                break;
            }
            nodeChecked = true;
        }

        return currentPos.isInRange(new Vec3d(currentNode.getX() + 0.5, isSurroundingEmpty ? entity.getY() + 0.5 : currentNode.getY(), currentNode.getZ() + 0.5), nodeReachProximity);
    }

    protected void startToFly(boolean shouldFly) {
        if (shouldFly){
            entity.addVelocity(0, 0.1, 0);
            entity.startToFly();
            jumpCount = 0;
        } else jumpCount++;
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        nodeReachProximity = entity.getWidth() / 2;
        if (!entity.isFlying()) nodeReachProximity = Math.clamp(nodeReachProximity, 0, .5f) ;
        if (distance <= nodeReachProximity) stop();
    }

    protected FlyingDragonMoveControl<T> getMoveControl() {
        return (FlyingDragonMoveControl<T>) entity.getMoveControl();
    }
}

