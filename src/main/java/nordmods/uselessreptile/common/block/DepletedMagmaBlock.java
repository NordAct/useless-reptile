package nordmods.uselessreptile.common.block;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherrackBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import nordmods.uselessreptile.common.init.URTags;
import org.jetbrains.annotations.NotNull;

public class DepletedMagmaBlock extends NetherrackBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public DepletedMagmaBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.dimensionTypeRegistration().is(URTags.DEPLETED_MAGMA_REGENERATES)) tickRegenerate(state, world, pos);
        if (world.getBlockState(pos.below()).getFluidState().is(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.above()).getFluidState().is(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.north()).getFluidState().is(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.south()).getFluidState().is(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.east()).getFluidState().is(ConventionalFluidTags.LAVA)
                || world.getBlockState(pos.west()).getFluidState().is(ConventionalFluidTags.LAVA)
        ) tickRegenerate(state, world, pos);
    }

    private void tickRegenerate(BlockState state, ServerLevel world, BlockPos pos) {
        int age = state.getValue(AGE);
        if (age < BlockStateProperties.MAX_AGE_2) world.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_NONE);
        else world.setBlockAndUpdate(pos, Blocks.MAGMA_BLOCK.defaultBlockState());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    protected @NotNull ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return Blocks.NETHERRACK.asItem().getDefaultInstance();
    }
}
