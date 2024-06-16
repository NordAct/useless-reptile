package nordmods.uselessreptile.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class BiomeTagGenerator extends FabricTagProvider<Biome>{
    public BiomeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_SPAWN_BLACKLIST);
        getOrCreateTagBuilder(URTags.LIGHTNING_CHASER_SPAWN_WHITELIST)
                .addOptionalTag(ConventionalBiomeTags.IS_OVERWORLD);

        getOrCreateTagBuilder(URTags.MOLECLAW_SPAWN_BLACKLIST);
        getOrCreateTagBuilder(URTags.MOLECLAW_SPAWN_WHITELIST)
                .addOptionalTag(ConventionalBiomeTags.IS_OVERWORLD);

        getOrCreateTagBuilder(URTags.WYVERN_SPAWN_BLACKLIST)
                .add(BiomeKeys.MANGROVE_SWAMP);
        getOrCreateTagBuilder(URTags.WYVERN_SPAWN_WHITELIST)
                .addOptionalTag(ConventionalBiomeTags.IS_SWAMP);

        getOrCreateTagBuilder(URTags.RIVER_PIKEHORN_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.IS_AQUATIC_ICY)
                .addOptionalTag(ConventionalBiomeTags.IS_SNOWY)
                .addOptionalTag(ConventionalBiomeTags.IS_COLD)
                .addOptionalTag(ConventionalBiomeTags.IS_ICY);
        getOrCreateTagBuilder(URTags.RIVER_PIKEHORN_SPAWN_WHITELIST)
                .addOptionalTag(ConventionalBiomeTags.IS_BEACH)
                .addOptionalTag(ConventionalBiomeTags.IS_RIVER)
                .addOptionalTag(ConventionalBiomeTags.IS_OCEAN);
    }
}
