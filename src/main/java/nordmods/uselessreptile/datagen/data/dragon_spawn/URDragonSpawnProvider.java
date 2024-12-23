package nordmods.uselessreptile.datagen.data.dragon_spawn;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.util.dragon_spawn.DragonSpawn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URDragonSpawnProvider implements DataProvider {

    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public URDragonSpawnProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        this.output = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "dragon_spawns");
        this.registryLookupFuture = registriesFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return registryLookupFuture.thenCompose((registryLookupFuture) -> {
            addSpawnEntries();
            List<CompletableFuture<?>> list = new ArrayList<>();
            DragonSpawn.getEntries().forEach(entry -> {
                String dragon = entry.getKey();
                List<DragonSpawn> spawns = entry.getValue();
                spawns.forEach(dragonSpawn -> {
                    Identifier id = dragonSpawn instanceof EventDragonSpawn ? getEventId(dragon, dragonSpawn.variant()) : getId(dragon, dragonSpawn.variant());
                    Path path = pathResolver.resolveJson(id);
                    list.add(DataProvider.writeCodecToPath(writer, registryLookupFuture, DragonSpawn.CODEC, dragonSpawn, path));
                });
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    protected Identifier getId(String dragon, String variant) {
        return UselessReptile.id(dragon + "/" + variant);
    }

    protected Identifier getEventId(String dragon, String variant) {
        return UselessReptile.id(dragon + "/event/" + variant);
    }

    /**
     * @see DragonSpawn#addSpawn(EntityType, DragonSpawn)
     * @see DragonSpawn.Builder#builder()
     */
    public void addSpawnEntries() {
        addWyvernEntry("green");
        addWyvernEntry("brown");

        addMoleclawEntry("black", 13);
        addMoleclawEntry("brown", 13);
        addMoleclawEntry("grey", 13);
        addMoleclawEntry("albino", 1);

        addRiverPikehornEntry("blue");
        addRiverPikehornEntry("dark_blue");
        addRiverPikehornEntry("green");
        addRiverPikehornEntry("dark_green");
        addRiverPikehornEntry("purple");
        addRiverPikehornEntry("dark_purple");
        addRiverPikehornEntry("teal");
        addRiverPikehornEntry("dark_teal");

        DragonSpawn lightningChaserBlueEvent = DragonSpawn.builder()
                .setVariant("blue")
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();

        DragonSpawn lightningChaserBrownEvent = DragonSpawn.builder()
                .setVariant("brown")
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();

        DragonSpawn lightningChaserGreyEvent = DragonSpawn.builder()
                .setVariant("grey")
                .setWeight(9)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                .addBannedBiomeTag(ConventionalBiomeTags.IS_DRY)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();

        DragonSpawn lightningChaserPurpleEvent = DragonSpawn.builder()
                .setVariant("purple")
                .setWeight(1)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .setMinAltitude(62)
                .addAllowedBlockTag(BlockTags.AIR)
                .build();

        DragonSpawn.addSpawn(UREntities.LIGHTNING_CHASER_ENTITY, getEventEntry(lightningChaserBlueEvent));
        DragonSpawn.addSpawn(UREntities.LIGHTNING_CHASER_ENTITY, getEventEntry(lightningChaserBrownEvent));
        DragonSpawn.addSpawn(UREntities.LIGHTNING_CHASER_ENTITY, getEventEntry(lightningChaserGreyEvent));
        DragonSpawn.addSpawn(UREntities.LIGHTNING_CHASER_ENTITY, getEventEntry(lightningChaserPurpleEvent));
    }

    protected void addWyvernEntry(String variant) {
        DragonSpawn spawn = DragonSpawn.builder()
                .setVariant(variant)
                .setWeight(1)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_SWAMP)
                .addBannedBiome(BiomeKeys.MANGROVE_SWAMP)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .build();
        DragonSpawn.addSpawn(UREntities.WYVERN_ENTITY, spawn);
    }

    protected void addMoleclawEntry(String variant, int weight) {
        DragonSpawn spawn = DragonSpawn.builder()
                .setVariant(variant)
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                .addAllowedBlockTag(ConventionalBlockTags.ORES)
                .addAllowedBlockTag(BlockTags.STONE_ORE_REPLACEABLES)
                .addAllowedBlockTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
                .addAllowedBlock(Blocks.DIRT.getRegistryEntry().registryKey())
                .addAllowedBlock(Blocks.GRAVEL.getRegistryEntry().registryKey())
                .build();
        DragonSpawn.addSpawn(UREntities.MOLECLAW_ENTITY, spawn);
    }

    protected void addRiverPikehornEntry(String variant) {
        DragonSpawn spawn = DragonSpawn.builder()
                .setVariant(variant)
                .setWeight(1)
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
        DragonSpawn.addSpawn(UREntities.RIVER_PIKEHORN_ENTITY, spawn);
    }

    protected EventDragonSpawn getEventEntry(DragonSpawn spawn) {
        return new EventDragonSpawn(spawn.variant(), spawn.conditions());
    }

    @Override
    public String getName() {
        return "Dragon Spawns";
    }
}
