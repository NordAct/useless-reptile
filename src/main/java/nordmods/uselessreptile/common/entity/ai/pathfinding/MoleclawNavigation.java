package nordmods.uselessreptile.common.entity.ai.pathfinding;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawNavigation extends MobNavigation {

    private final MoleclawEntity entity;

    public MoleclawNavigation(MoleclawEntity mobEntity, World world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    protected void continueFollowingPath() {
        if (currentPath == null) return;

        Vec3d vec3d = this.getPos();
        int index = this.currentPath.getCurrentNodeIndex();
        Vec3d nodePos = Vec3d.ofBottomCenter(this.currentPath.getNodePos(index));
        this.nodeReachProximity = (this.entity.getHeight() + 1) * 0.5f;

        double d = Math.abs(this.entity.getX() - nodePos.getX());
        double yDiff = nodePos.getY() - this.entity.getY();
        double f = Math.abs(this.entity.getZ() - nodePos.getZ());

        boolean bl = d < (double)this.nodeReachProximity && f < (double)this.nodeReachProximity &&  yDiff < 1.0D && yDiff > -5.0D;

        if (bl || entity.canJumpToNextPathNode(this.currentPath.getNode(index).type) && this.shouldJumpToNextNode(vec3d)) {
            this.currentPath.next();
        }

        this.checkTimeouts(vec3d);
    }

    private boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (currentPath == null) return false;

        if (this.currentPath.getCurrentNodeIndex() + 1 >= this.currentPath.getLength()) return false;
        Vec3d vec3d = Vec3d.ofBottomCenter(this.currentPath.getCurrentNodePos());
        if (!currentPos.isInRange(vec3d, this.entity.getWidth())) return false;
        Vec3d vec3d2 = Vec3d.ofBottomCenter(this.currentPath.getNodePos(this.currentPath.getCurrentNodeIndex() + 1));
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        Vec3d vec3d4 = currentPos.subtract(vec3d);
        return vec3d3.dotProduct(vec3d4) > 0.0D;
    }

    @Override
    protected void adjustPath() {
        if (currentPath == null) return;

        super.adjustPath();
        if (!entity.isPanicking() && !entity.hasHelmet()) {
            for (int i = 0; i < this.currentPath.getLength(); ++i) {
                PathNode pathNode = this.currentPath.getNode(i);
                if (entity.isTooBrightAtPos(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
                    this.currentPath.setLength(i);
                    return;
                }
            }
        }
    }
}
