package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.BiomeKeys;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URSpawnConditionsProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final List<Pair<Identifier, List<DragonSpawnConditions>>> holder = new ArrayList<>();

    public URSpawnConditionsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "ur_dragon_variant/spawn_conditions");
        this.registryLookupFuture = registriesFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addSpawnEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            holder.forEach(entry -> {
                Path path = pathResolver.resolveJson(entry.getLeft());
                list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonSpawnConditions.CODEC.listOf(), entry.getRight(), path));
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
        addSpawn("cannot_sapwn", Collections.singletonList(DragonSpawnConditions.builder().setWeight(0).build()));
    }

    protected void addWyvernEntry(String name, int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_SWAMP)
                .addBannedBiome(BiomeKeys.MANGROVE_SWAMP)
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
                .addAllowedBlock(Blocks.DIRT.getRegistryEntry().registryKey())
                .addAllowedBlock(Blocks.GRAVEL.getRegistryEntry().registryKey())
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
                .addAllowedBlock(Blocks.GRAVEL.getRegistryEntry().registryKey())
                .setMinAltitude(62)
                .build();

        addSpawn("river_pikehorn/" + name, Collections.singletonList(spawn));
    }

    protected void addSpawn(String name, List<DragonSpawnConditions> conditions) {
        holder.add(new Pair<>(getId(name), conditions));
    }

    @Override
    public String getName() {
        return "Spawn Conditions";
    }
}
