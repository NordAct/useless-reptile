package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class URBiomeTagProvider extends FabricTagProvider<Biome>{
    public URBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(URTags.LIGHTNING_CHASER_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.id());

        getTagBuilder(URTags.MOLECLAW_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.id());

        getTagBuilder(URTags.WYVERN_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.id());

        getTagBuilder(URTags.RIVER_PIKEHORN_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.id());
    }
}
