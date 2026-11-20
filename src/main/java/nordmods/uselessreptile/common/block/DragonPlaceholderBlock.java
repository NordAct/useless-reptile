package nordmods.uselessreptile.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import nordmods.uselessreptile.common.init.URBlockEntityTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DragonPlaceholderBlock extends BaseEntityBlock {
    public static final MapCodec<DragonPlaceholderBlock> CODEC = simpleCodec(DragonPlaceholderBlock::new);
    public static final BooleanProperty CAN_CREATE_DRAGON = BooleanProperty.create("can_create_dragon");
    public DragonPlaceholderBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CAN_CREATE_DRAGON, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos worldPosition, @NonNull BlockState blockState) {
        return new  DragonPlaceholderBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CAN_CREATE_DRAGON);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> type) {
        return level instanceof ServerLevel ? createTickerHelper(type, URBlockEntityTypes.DRAGON_PLACEHOLDER, (_, pos, _, entity) -> entity.spawnDragon(pos)) : null;
    }
}
