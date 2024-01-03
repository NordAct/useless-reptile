package nordmods.uselessreptile.common.entity.ai.pathfinding;

import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonNavigation extends MobNavigation {
    private final URDragonEntity entity;

    public DragonNavigation(URDragonEntity mobEntity, World world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    public void tick() {
        BlockPos target = getTargetPos();
        boolean isFullBlock = entity.getSteppingBlockState().isFullCube(entity.getWorld(), entity.getSteppingPos());
        if (NavigationConditions.isSolidAt(entity, entity.getBlockPos()) && isFullBlock) entity.getJumpControl().setActive();

        if (!isIdle() && target != null) {
            tickCount += 2;
            continueFollowingPath();
            moveOrStop(target);
            if (!isIdle()) {
                double yDiffNode = currentPath.getCurrentNode().getPos().getY() - entity.getY();
                if (yDiffNode > entity.getStepHeight() && entity.horizontalCollision) entity.getJumpControl().setActive();
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

        boolean bl = d < (double)nodeReachProximity && f < (double)nodeReachProximity &&  yDiff <= entity.getStepHeight() && yDiff > -3.0D;

        if (bl || canJumpToNext(currentPath.getNode(index).type) && shouldJumpToNextNode(vec3d)) currentPath.next();

        checkTimeouts(vec3d);
    }

    protected boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (currentPath.getCurrentNodeIndex() + 1 >= currentPath.getLength()) return false;
        Vec3d vec3d = Vec3d.ofBottomCenter(currentPath.getCurrentNodePos());
        if (!currentPos.isInRange(vec3d, MathHelper.clamp(entity.getWidth(), 0, 1.5))) return false;
        if (canPathDirectlyThrough(currentPos, currentPath.getNodePosition(entity))) return true;
        Vec3d vec3d2 = Vec3d.ofBottomCenter(currentPath.getNodePos(currentPath.getCurrentNodeIndex() + 1));
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        Vec3d vec3d4 = currentPos.subtract(vec3d);
        return vec3d3.dotProduct(vec3d4) > 0.0D;
    }

    protected void moveOrStop(BlockPos target) {
        double distance = entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        nodeReachProximity = (float) Math.sqrt(entity.getRotationSpeed() * entity.getWidth());
        entity.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1);
        if (distance <= nodeReachProximity) entity.getNavigation().stop();
    }
}
