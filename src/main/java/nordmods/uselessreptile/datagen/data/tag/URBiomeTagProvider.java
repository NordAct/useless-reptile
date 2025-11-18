package nordmods.uselessreptile.datagen.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import nordmods.uselessreptile.common.init.URTags;

import java.util.concurrent.CompletableFuture;

public class URBiomeTagProvider extends FabricTagProvider<Biome>{
    public URBiomeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateRawBuilder(URTags.LIGHTNING_CHASER_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.location());

        getOrCreateRawBuilder(URTags.MOLECLAW_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.location());

        getOrCreateRawBuilder(URTags.WYVERN_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.location());

        getOrCreateRawBuilder(URTags.RIVER_PIKEHORN_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.location());

        getOrCreateRawBuilder(URTags.MAGMAMUNCHER_SPAWN_BLACKLIST)
                .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS.location());
    }
}
