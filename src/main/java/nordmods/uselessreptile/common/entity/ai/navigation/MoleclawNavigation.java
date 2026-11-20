package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import nordmods.uselessreptile.common.entity.Moleclaw;

public class MoleclawNavigation extends LandDragonNavigation<Moleclaw> {
    public MoleclawNavigation(Moleclaw mobEntity, Level world) {
        super(mobEntity, world);
    }

    @Override
    protected void followThePath() {
        if (path == null) return;
        super.followThePath();
    }

    @Override
    protected void trimPath() {
        if (path == null) return;

        super.trimPath();
        if (!mob.isPanicking() && !mob.hasLightProtection()) {
            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                Node pathNode = this.path.getNode(i);
                if (mob.isTooBrightAtPos(new BlockPos(pathNode.x, pathNode.y, pathNode.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }
    }
}
