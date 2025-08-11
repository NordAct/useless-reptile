package nordmods.uselessreptile.common.block;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import nordmods.uselessreptile.common.init.URTags;

public class DepletedMagmaBlock extends NetherrackBlock {
    public static final IntProperty AGE = Properties.AGE_2;
    public DepletedMagmaBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(AGE, 0));
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getDimensionEntry().isIn(URTags.DEPLETED_MAGMA_REGENERATES)) tickRegenerate(state, world, pos);
        if (world.getBlockState(pos.down()).getFluidState().isIn(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.up()).getFluidState().isIn(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.north()).getFluidState().isIn(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.south()).getFluidState().isIn(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.east()).getFluidState().isIn(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.west()).getFluidState().isIn(ConventionalFluidTags.LAVA)
        ) tickRegenerate(state, world, pos);
    }

    private void tickRegenerate(BlockState state, ServerWorld world, BlockPos pos) {
        int age = state.get(AGE);
        if (age < Properties.AGE_2_MAX) world.setBlockState(pos, state.with(AGE, age + 1), Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        else world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return Blocks.NETHERRACK.asItem().getDefaultStack();
    }
}
