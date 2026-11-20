package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.block.DragonPlaceholderBlockEntity;

public class URBlockEntityTypes {
    public static final BlockEntityType<DragonPlaceholderBlockEntity> DRAGON_PLACEHOLDER = register("dragon_placeholder", DragonPlaceholderBlockEntity::new, URBlocks.DRAGON_PLACEHOLDER);

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block validBlocks) {
        return (BlockEntityType<T>) Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                UselessReptile.id(id),
                FabricBlockEntityTypeBuilder.create(factory, validBlocks).build());
    }

    public static void init() {}
}
