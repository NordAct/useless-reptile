package nordmods.uselessreptile.common.entity.ai.navigation;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class MagmamuncherNavigation extends DragonNavigation {
    public MagmamuncherNavigation(URDragonEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public boolean canJumpToNext(PathNodeType nodeType) {
        return nodeType != PathNodeType.DANGER_OTHER && nodeType != PathNodeType.WALKABLE_DOOR;
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new MagmamuncherPathNodeMaker();
        return new PathNodeNavigator(nodeMaker, range);
    }

    public static class MagmamuncherPathNodeMaker extends LandPathNodeMaker {
        @Override
        public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
            BlockState blockState = context.getBlockState(new BlockPos(x, y, z));
            return blockState.isOf(Blocks.MAGMA_BLOCK) ? PathNodeType.BLOCKED : super.getDefaultNodeType(context, x, y, z);
        }
    }
}
