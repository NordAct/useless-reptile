package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URSpawnConditionsProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;
    private final List<Tuple<Identifier, List<DragonSpawnConditions>>> holder = new ArrayList<>();

    public URSpawnConditionsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this.output = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "uselessreptile/spawn_conditions");
        this.registryLookupFuture = registriesFuture;
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addSpawnEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(entry -> {
                Path path = pathResolver.json(entry.getA());
                list.add(DataProvider.saveStable(writer, registryLookupFuture, DragonSpawnConditions.CODEC.listOf(), entry.getB(), path));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected Identifier getId(String name) {
        return UselessReptile.id(name);
    }

    public void addSpawnEntries() {
        addWyvernEntry("default", 1);
        addMoleclawEntry("default", 13);
        addMoleclawEntry("rare", 1);
        addRiverPikehornEntry("default", 1);

        DragonSpawnConditions lightningChaserBlueEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();
        addSpawn("lightning_chaser/blue", Collections.singletonList(lightningChaserBlueEvent));

        DragonSpawnConditions lightningChaserBrownEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();
        addSpawn("lightning_chaser/brown", Collections.singletonList(lightningChaserBrownEvent));

        DragonSpawnConditions lightningChaserGreyEvent = DragonSpawnConditions.builder()
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();
        addSpawn("lightning_chaser/grey", Collections.singletonList(lightningChaserGreyEvent));

        DragonSpawnConditions lightningChaserPurpleEvent = DragonSpawnConditions.builder()
                .setWeight(1)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();
        addSpawn("lightning_chaser/purple", Collections.singletonList(lightningChaserPurpleEvent));

        addMagmamuncherEntry("default", 1);

        addSpawn("cannot_sapwn", Collections.singletonList(DragonSpawnConditions.builder().setWeight(0).build()));
    }

    protected void addWyvernEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_SWAMP)
                .addBannedBiome(Biomes.MANGROVE_SWAMP)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .build();

        addSpawn("wyvern/" + name, Collections.singletonList(spawn));
    }

    protected void addMoleclawEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addAllowedBlockTag(ConventionalBlockTags.ORES)
                .addAllowedBlockTag(BlockTags.STONE_ORE_REPLACEABLES)
                .addAllowedBlockTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .addAllowedBlock(Blocks.DIRT.builtInRegistryHolder().key())
                .addAllowedBlock(Blocks.GRAVEL.builtInRegistryHolder().key())
                .build();
        addSpawn("moleclaw/" + name, Collections.singletonList(spawn));
    }

    protected void addRiverPikehornEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_BEACH)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_RIVER)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_AQUATIC_ICY)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_SNOWY)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_COLD)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_ICY)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addAllowedBlockTag(BlockTags.SAND)
                .addAllowedBlock(Blocks.GRAVEL.builtInRegistryHolder().key())
                .setMinAltitude(62)
                .build();

        addSpawn("river_pikehorn/" + name, Collections.singletonList(spawn));
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
        addSpawn("magmamuncher/" + name, Collections.singletonList(spawn));
    }

    protected void addSpawn(String name, List<DragonSpawnConditions> conditions) {
        holder.add(new Tuple<>(getId(name), conditions));
    }

    @Override
    public @NonNull String getName() {
        return "Spawn Conditions";
    }
}
