package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.Moleclaw;

public class MoleclawNavigation extends DragonNavigation {

    private final Moleclaw entity;

    public MoleclawNavigation(Moleclaw mobEntity, Level world) {
        super(mobEntity, world);
        this.entity = mobEntity;
    }

    @Override
    protected void followThePath() {
        if (path == null) return;
        super.followThePath();
    }

    protected boolean shouldTargetNextNodeInDirection(Vec3 currentPos) {
        if (path == null) return false;
        return super.shouldTargetNextNodeInDirection(currentPos);
    }

    @Override
    protected void trimPath() {
        if (path == null) return;

        super.trimPath();
        if (!entity.isPanicking() && !entity.hasLightProtection()) {
            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                Node pathNode = this.path.getNode(i);
                if (entity.isTooBrightAtPos(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }
    }
}
