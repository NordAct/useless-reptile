package nordmods.uselessreptile.common.entity.ai.pathfinding;

import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.BirdPathNodeMaker;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public class FlyingDragonNavigation<T extends URDragonEntity & FlyingDragon> extends BirdNavigation {

    private final T entity;
    private int jumpCount;

    public FlyingDragonNavigation(T entity, World world) {
        super(entity, world);
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (entity.hasControllingPassenger() || entity.hasVehicle()) return;

        BlockPos target = getTargetPos();
        boolean isFullBlock = entity.getSteppingBlockState().isFullCube(entity.getWorld(), entity.getSteppingPos());
        if (NavigationConditions.isSolidAt(entity, entity.getBlockPos()) && isFullBlock) entity.getJumpControl().setActive();
        if (entity.isTouchingWater() && entity.getFluidHeight(FluidTags.WATER) > entity.getSwimHeight() && !entity.hasTargetInWater() || entity.isInLava())
            startToFly(jumpCount > 9 || entity.isInLava());
        entity.setPathfindingPenalty(PathNodeType.WATER, entity.isFlying() && !entity.hasTargetInWater() ? 8 : 0);

        if (!isIdle() && target != null) {
            if (entity.isFlying()) {
                nodeMaker = new BirdPathNodeMaker();
                jumpCount = 0;
                moveOrStop(target);
            }
            else {
                nodeMaker = new LandPathNodeMaker();
                tickCount += 2;
                continueFollowingPath();
                moveOrStop(target);

                if (!isIdle()) {
                    double yDiffNode = currentPath.getCurrentNode().getPos().getY() - entity.getY();
                    double yDiffTarget = target.getY() - entity.getY();
                    double xDiffTarget = Math.pow(entity.getX() - target.getX(), 2);
                    double zDiffTarget = Math.pow(entity.getZ() - target.getZ(), 2);
                    boolean shouldFlyUp = jumpCount > 9 || yDiffTarget > 3 && Math.sqrt(xDiffTarget + zDiffTarget) < 16 || yDiffTarget > 8 || Math.sqrt(xDiffTarget + zDiffTarget) > 64;
                    if (yDiffNode > 0.5 && entity.horizontalCollision || shouldFlyUp && !entity.hasTargetInWater()) {
                        entity.getJumpControl().setActive();
                        startToFly(shouldFlyUp);
                    }
                }
            }
        }
    }

    @Override
    protected void continueFollowingPath() {
        Vec3d vec3d = getPos();
        int index = currentPath.getCurrentNodeIndex();
        Vec3d nodePos = Vec3d.ofBottomCenter(currentPath.getNodePos(index));

        double d = Math.abs(entity.getX() - nodePos.getX());
        double yDiff = nodePos.getY() - entity.getY();
        double f = Math.abs(entity.getZ() - nodePos.getZ());

        boolean bl = d < (double)nodeReachProximity && f < (double)nodeReachProximity &&  yDiff < 1.0D && yDiff > -5.0D;

        if (bl || canJumpToNext(currentPath.getNode(index).type) && shouldJumpToNextNode(vec3d)) {
            currentPath.next();
            jumpCount = 0;
        }

        checkTimeouts(vec3d);
    }

    private boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (currentPath.getCurrentNodeIndex() + 1 >= currentPath.getLength()) return false;
        Vec3d vec3d = Vec3d.ofBottomCenter(currentPath.getCurrentNodePos());
        if (!currentPos.isInRange(vec3d, MathHelper.clamp(entity.getWidth(), 0, 2))) return false;
        if (canPathDirectlyThrough(currentPos, currentPath.getNodePosition(entity))) return true;
        Vec3d vec3d2 = Vec3d.ofBottomCenter(currentPath.getNodePos(currentPath.getCurrentNodeIndex() + 1));
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        Vec3d vec3d4 = currentPos.subtract(vec3d);
        return vec3d3.dotProduct(vec3d4) > 0.0D;
    }

    private void startToFly(boolean shouldFly) {
        if (shouldFly){
            entity.addVelocity(0, 0.1, 0);
            entity.startToFly();
        } else jumpCount++;
    }

    private void moveOrStop(BlockPos target) {
        double distance = entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        nodeReachProximity = (float) Math.sqrt(entity.getRotationSpeed() * entity.getWidth());
        entity.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1);
        if (distance <= nodeReachProximity) entity.getNavigation().stop();
    }
}

