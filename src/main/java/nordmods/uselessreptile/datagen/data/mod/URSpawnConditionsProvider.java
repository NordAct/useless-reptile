package nordmods.uselessreptile.datagen.data.mod;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_variant.spawn.DragonSpawnConditions;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class URSpawnConditionsProvider extends FabricDynamicRegistryProvider {
    public static final ResourceKey<List<DragonSpawnConditions>> WYVERN = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("wyvern/default"));
    public static final ResourceKey<List<DragonSpawnConditions>> RIVER_PIKEHORN = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("river_pikehorn/default"));
    public static final ResourceKey<List<DragonSpawnConditions>> LIGHTNING_CHASER_GREY = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("lightning_chaser/grey"));
    public static final ResourceKey<List<DragonSpawnConditions>> LIGHTNING_CHASER_BLUE = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("lightning_chaser/blue"));
    public static final ResourceKey<List<DragonSpawnConditions>> LIGHTNING_CHASER_BROWN = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("lightning_chaser/brown"));
    public static final ResourceKey<List<DragonSpawnConditions>> LIGHTNING_CHASER_PURPLE = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("lightning_chaser/purple"));
    public static final ResourceKey<List<DragonSpawnConditions>> MOLECLAW = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("moleclaw/default"));
    public static final ResourceKey<List<DragonSpawnConditions>> MOLECLAW_RARE = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("moleclaw/rare"));
    public static final ResourceKey<List<DragonSpawnConditions>> MAGMAMUNCHER = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("magmamuncher/default"));
    public static final ResourceKey<List<DragonSpawnConditions>> CANNOT_SPAWN = ResourceKey.create(URResourceKeys.DRAGON_SPAWN_CONDITIONS, UselessReptile.id("cannot_spawn"));

    public URSpawnConditionsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.@NonNull Provider provider, @NonNull Entries entries) {
        entries.addAll(provider.lookupOrThrow(URResourceKeys.DRAGON_SPAWN_CONDITIONS));
    }

    public static void register(BootstrapContext<List<DragonSpawnConditions>> bootstrapContext) {
        bootstrapContext.register(CANNOT_SPAWN, List.of());
        bootstrapContext.register(WYVERN, makeWyvernEntry(1));
        bootstrapContext.register(RIVER_PIKEHORN, makeRiverPikehornEntry(1));
        bootstrapContext.register(MOLECLAW, makeMoleclawEntry(13));
        bootstrapContext.register(MOLECLAW_RARE, makeMoleclawEntry(1));
        bootstrapContext.register(MAGMAMUNCHER, makeMagmamuncherEntry(1));
        bootstrapContext.register(
                LIGHTNING_CHASER_GREY,
                List.of(
                        DragonSpawnConditions.builder()
                                .setWeight(9)
                                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                                .addBannedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                                .addBannedBiomeTag(ConventionalBiomeTags.IS_DRY)
                                .setMinAltitude(62)
                                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                                .build()
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER_BLUE,
                List.of(
                        DragonSpawnConditions.builder()
                                .setWeight(9)
                                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OCEAN)
                                .setMinAltitude(62)
                                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                                .build()
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER_BROWN,
                List.of(
                        DragonSpawnConditions.builder()
                                .setWeight(9)
                                .addAllowedBiomeTag(ConventionalBiomeTags.IS_DRY)
                                .setMinAltitude(62)
                                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                                .build()
                )
        );
        bootstrapContext.register(
                LIGHTNING_CHASER_PURPLE,
                List.of(
                        DragonSpawnConditions.builder()
                                .setWeight(1)
                                .addAllowedBiomeTag(ConventionalBiomeTags.IS_OVERWORLD)
                                .setMinAltitude(62)
                                .addAllowedSpawnReasons(EntitySpawnReason.EVENT, EntitySpawnReason.STRUCTURE)
                                .build()
                )
        );
    }

    protected static List<DragonSpawnConditions> makeWyvernEntry(int weight) {
        DragonSpawnConditions spawn = DragonSpawnConditions.builder()
                .setWeight(weight)
                .addAllowedBiomeTag(ConventionalBiomeTags.IS_SWAMP)
                .addBannedBiome(Biomes.MANGROVE_SWAMP)
                .addAllowedBlockTag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .setSpacing(32, 0)
                .build();

        return List.of(spawn);
    }

    protected static List<DragonSpawnConditions> makeMoleclawEntry(int weight) {
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
        return List.of(spawn);
    }

    protected static List<DragonSpawnConditions> makeRiverPikehornEntry(int weight) {
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

        return List.of(spawn);
    }

    protected static List<DragonSpawnConditions> makeMagmamuncherEntry(int weight) {
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
        return List.of(spawn);
    }

    @Override
    public @NonNull String getName() {
        return "Spawn Conditions";
    }
}
