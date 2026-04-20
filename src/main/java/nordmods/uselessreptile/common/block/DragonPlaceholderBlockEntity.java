package nordmods.uselessreptile.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.uselessreptile.common.init.URBlockEntityTypes;
import nordmods.uselessreptile.common.init.UREntities;
import org.jspecify.annotations.NonNull;

public class DragonPlaceholderBlockEntity extends BlockEntity {
    private EntityType<?> dragon = UREntities.WYVERN;
    public DragonPlaceholderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(URBlockEntityTypes.DRAGON_PLACEHOLDER, worldPosition, blockState);
    }

    public void spawnDragon(BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = serverLevel.getBlockState(pos);
            if (!state.hasProperty(DragonPlaceholderBlock.CAN_CREATE_DRAGON) || !state.getValue(DragonPlaceholderBlock.CAN_CREATE_DRAGON)) return;
            Entity e = dragon.create(serverLevel, null, pos, EntitySpawnReason.STRUCTURE, false, false);
            serverLevel.addFreshEntityWithPassengers(e);
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL, 1);
        }
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        spawnDragon(pos);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        dragon = input.read("Dragon", EntityType.CODEC).orElse(UREntities.WYVERN);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("Dragon", EntityType.CODEC, dragon);
    }
}
