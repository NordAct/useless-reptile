package nordmods.uselessreptile.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(URTags.DRAGON_UNBREAKABLE);

        getOrCreateTagBuilder(URTags.MOLECLAW_SPAWNABLE_ON)
                .addOptionalTag(ConventionalBlockTags.ORES)
                .addOptionalTag(BlockTags.STONE_ORE_REPLACEABLES)
                .addOptionalTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .add(Blocks.DIRT)
                .add(Blocks.GRAVEL);

        getOrCreateTagBuilder(URTags.WYVERN_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON);

        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON);

        getOrCreateTagBuilder(URTags.RIVER_PIKEHORN_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addOptionalTag(BlockTags.SAND);
    }
}
