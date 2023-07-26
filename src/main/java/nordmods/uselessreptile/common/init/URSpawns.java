package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;

public class URSpawns {

    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors
                .tag(URTags.SWAMP_WYVERN_SPAWN_WHITELIST)
                .and(BiomeSelectors.includeByKey(BiomeKeys.MANGROVE_SWAMP).negate()),
                SpawnGroup.CREATURE,
                UREntities.WYVERN_ENTITY,
                URConfig.getConfig().wyvernSpawnWeight,
                1, 1);

        BiomeModifications.addSpawn(BiomeSelectors
                .foundInOverworld()
                .and(BiomeSelectors.tag(URTags.MOLECLAW_SPAWN_BLACKLIST).negate()),
                SpawnGroup.AMBIENT,
                UREntities.MOLECLAW_ENTITY,
                URConfig.getConfig().moleclawSpawnWeight,
                1, 1);

        BiomeModifications.addSpawn(BiomeSelectors
                        .tag(URTags.RIVER_PIKEHORN_SPAWN_WHITELIST)
                        .and(BiomeSelectors.tag(ConventionalBiomeTags.AQUATIC_ICY).negate())
                        .and(BiomeSelectors.tag(ConventionalBiomeTags.SNOWY).negate())
                        .and(BiomeSelectors.tag(ConventionalBiomeTags.CLIMATE_COLD).negate())
                        .and(BiomeSelectors.tag(ConventionalBiomeTags.ICY).negate()),
                SpawnGroup.CREATURE,
                UREntities.RIVER_PIKEHORN_ENTITY,
                URConfig.getConfig().pikehornSpawnWeight,
                2, 6);

        SpawnRestriction.register(UREntities.WYVERN_ENTITY, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WyvernEntity::canMobSpawn);
        SpawnRestriction.register(UREntities.MOLECLAW_ENTITY, SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MoleclawEntity::canMoleclawSpawn);
        SpawnRestriction.register(UREntities.RIVER_PIKEHORN_ENTITY, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RiverPikehornEntity::canMobSpawn);
    }
}
