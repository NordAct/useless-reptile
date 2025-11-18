package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class MagmamuncherNavigation extends DragonNavigation {
    public MagmamuncherNavigation(URDragonEntity mobEntity, Level world) {
        super(mobEntity, world);
    }

    @Override
    public boolean canCutCorner(PathType nodeType) {
        return nodeType != PathType.DANGER_OTHER && nodeType != PathType.WALKABLE_DOOR;
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.nodeEvaluator = new MagmamuncherPathNodeMaker();
        return new PathFinder(nodeEvaluator, range);
    }

    public static class MagmamuncherPathNodeMaker extends WalkNodeEvaluator {
        @Override
        public PathType getPathType(PathfindingContext context, int x, int y, int z) {
            BlockState blockState = context.getBlockState(new BlockPos(x, y, z));
            return blockState.is(Blocks.MAGMA_BLOCK) ? PathType.BLOCKED : super.getPathType(context, x, y, z);
        }
    }
}
