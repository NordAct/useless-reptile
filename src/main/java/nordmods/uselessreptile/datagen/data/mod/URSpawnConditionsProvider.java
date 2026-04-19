package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import nordmods.uselessreptile.datagen.data.URAbstractDataProvider;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URSpawnConditionsProvider extends URAbstractDataProvider<List<DragonSpawnConditions>> {

    public URSpawnConditionsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, DragonSpawnConditions.CODEC.listOf(), "uselessreptile/spawn_conditions");
    }

    @Override
    public void addEntries(HolderLookup.Provider provider) {
        addWyvernEntry("default", 1);
        addMoleclawEntry("default", 13);
        addMoleclawEntry("rare", 1);
        addRiverPikehornEntry("default", 1);

        DragonSpawnConditions lightningChaserBlueEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .setMinAltitude(62)
                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                .build();
        addEntry(UselessReptile.id("lightning_chaser/blue"), Collections.singletonList(lightningChaserBlueEvent));

        DragonSpawnConditions lightningChaserBrownEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                .build();
        addEntry(UselessReptile.id("lightning_chaser/brown"), Collections.singletonList(lightningChaserBrownEvent));

        DragonSpawnConditions lightningChaserGreyEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                .build();
        addEntry(UselessReptile.id("lightning_chaser/grey"), Collections.singletonList(lightningChaserGreyEvent));

        DragonSpawnConditions lightningChaserPurpleEvent = DragonSpawnConditions.builder()
                .setWeight(1)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .setMinAltitude(62)
                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                .build();
        addEntry(UselessReptile.id("lightning_chaser/purple"), Collections.singletonList(lightningChaserPurpleEvent));

        addMagmamuncherEntry("default", 1);

        addEntry(UselessReptile.id("cannot_sapwn"), Collections.singletonList(DragonSpawnConditions.builder().setWeight(0).build()));
    }

    protected void addWyvernEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_SWAMP)
                .addBannedBiome(Biomes.MANGROVE_SWAMP)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .setSpacing(32, 0)
                .build();

        addEntry(UselessReptile.id("wyvern/" + name), Collections.singletonList(spawn));
    }

    protected void addMoleclawEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addAllowedBlockTag(ConventionalBlockTags.ORES)
                .addAllowedBlockTag(ConventionalBlockTags.STONES)
                .addAllowedBlock(Blocks.DIRT.builtInRegistryHolder().key())
                .addAllowedBlock(Blocks.GRAVEL.builtInRegistryHolder().key())
                .setMaxSkyLightLevel(0)
                .setMaxBlockLightLevel(0)
                .build();
        addEntry(UselessReptile.id("moleclaw/" + name), Collections.singletonList(spawn));
    }

    protected void addRiverPikehornEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_RIVER)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_BEACH)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_AQUATIC_ICY)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_SNOWY)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_COLD)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_ICY)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addAllowedBlockTag(ConventionalBlockTags.SANDS)
                .addAllowedBlockTag(ConventionalBlockTags.GRAVELS)
                .setMinAltitude(62)
                .setMinSkyLightLevel(14)
                .setSpacing(8, 3)
                .build();

        addEntry(UselessReptile.id("river_pikehorn/" + name), Collections.singletonList(spawn));
    }

    protected void addMagmamuncherEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiome(Biomes.NETHER_WASTES)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_NETHER_FOREST)
                .addAllowedBlockTag(ConventionalBlockTags.NETHERRACKS)
                .addAllowedBlockTag(ConventionalBlockTags.GRAVELS)
                .addAllowedBlock(Blocks.MAGMA_BLOCK.builtInRegistryHolder().key())
                .setMinAltitude(26)
                .setMaxAltitude(37)
                .build();
        addEntry(UselessReptile.id("magmamuncher/" + name), Collections.singletonList(spawn));
    }

    @Override
    public @NonNull String getName() {
        return "Spawn Conditions";
    }
}
