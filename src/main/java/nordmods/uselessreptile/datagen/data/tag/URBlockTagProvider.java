package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class URBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public URBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(URTags.DRAGON_UNBREAKABLE)
                .addOptionalTag(BlockTags.AIR.id());
        getTagBuilder(URTags.LIGHTNING_BREATH_ALWAYS_BREAKS)
                .addOptionalTag(BlockTags.LEAVES.id())
                .addOptionalTag(BlockTags.REPLACEABLE.id())
                .addOptionalTag(BlockTags.FLOWERS.id())
                .addOptionalTag(BlockTags.WOOL_CARPETS.id())
                .addOptionalTag(BlockTags.WOOL.id())
                .add(Blocks.MOSS_BLOCK.getRegistryEntry().registryKey().getValue())
                .add(Blocks.MOSS_CARPET.getRegistryEntry().registryKey().getValue())
                .add(Blocks.MUSHROOM_STEM.getRegistryEntry().registryKey().getValue())
                .add(Blocks.BROWN_MUSHROOM_BLOCK.getRegistryEntry().registryKey().getValue())
                .add(Blocks.RED_MUSHROOM_BLOCK.getRegistryEntry().registryKey().getValue())
                .addOptionalTag(BlockTags.SNOW.id());
    }
}
