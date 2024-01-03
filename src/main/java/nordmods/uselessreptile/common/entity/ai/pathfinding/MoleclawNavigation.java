package nordmods.uselessreptile.common.entity.ai.pathfinding;

import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawNavigation extends DragonNavigation {

    private final MoleclawEntity entity;

    public MoleclawNavigation(MoleclawEntity mobEntity, World world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    protected void continueFollowingPath() {
        if (currentPath == null) return;
        super.continueFollowingPath();
    }

    protected boolean shouldJumpToNextNode(Vec3d currentPos) {
        if (currentPath == null) return false;
        return super.shouldJumpToNextNode(currentPos);
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
