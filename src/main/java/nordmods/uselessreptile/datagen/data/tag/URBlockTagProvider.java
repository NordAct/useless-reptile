package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class URBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public URBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
        getOrCreateRawBuilder(URTags.DRAGON_UNBREAKABLE)
                .addOptionalTag(BlockTags.AIR.location());
        getOrCreateRawBuilder(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS)
                .addOptionalTag(BlockTags.LEAVES.location())
                .addOptionalTag(BlockTags.REPLACEABLE.location())
                .addOptionalTag(BlockTags.FLOWERS.location())
                .addOptionalTag(BlockTags.WOOL_CARPETS.location())
                .addOptionalTag(BlockTags.WOOL.location())
                .addElement(Blocks.MOSS_BLOCK.builtInRegistryHolder().key().identifier())
                .addElement(Blocks.MOSS_CARPET.builtInRegistryHolder().key().identifier())
                .addElement(Blocks.MUSHROOM_STEM.builtInRegistryHolder().key().identifier())
                .addElement(Blocks.BROWN_MUSHROOM_BLOCK.builtInRegistryHolder().key().identifier())
                .addElement(Blocks.RED_MUSHROOM_BLOCK.builtInRegistryHolder().key().identifier())
                .addOptionalTag(BlockTags.SNOW.location());

        getOrCreateRawBuilder(BlockTags.INFINIBURN_END)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
        getOrCreateRawBuilder(BlockTags.INFINIBURN_NETHER)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
        getOrCreateRawBuilder(BlockTags.INFINIBURN_OVERWORLD)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
        getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
        getOrCreateRawBuilder(ConventionalBlockTags.NETHERRACKS)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
        getOrCreateRawBuilder(BlockTags.SCULK_REPLACEABLE)
                .addElement(URBlocks.DEPLETED_MAGMA.builtInRegistryHolder().key().identifier());
    }
}
