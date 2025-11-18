package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.LightningChaser;
import nordmods.uselessreptile.common.entity.Moleclaw;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.util.URMobCategory;

public class URSpawns {
    public static void init() {
        if (URConfig.getConfig().naturalWyvernSpawn) {
            BiomeModifications.addSpawn(BiomeSelectors.tag(URTags.WYVERN_SPAWN_BLACKLIST).negate(),
                    URMobCategory.DRAGON.mobCategory,
                    UREntities.WYVERN_ENTITY,
                    1,
                    URConfig.getConfig().wyvernMinGroupSize, URConfig.getConfig().wyvernMaxGroupSize);
            SpawnPlacements.register(UREntities.WYVERN_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, URDragonEntity::canDragonSpawn);
        }

        if (URConfig.getConfig().naturalMoleclawSpawn) {
            BiomeModifications.addSpawn(BiomeSelectors.tag(URTags.MOLECLAW_SPAWN_BLACKLIST).negate(),
                    URMobCategory.UNDERGROUND_DRAGON.mobCategory,
                    UREntities.MOLECLAW_ENTITY,
                    1,
                    URConfig.getConfig().moleclawMinGroupSize, URConfig.getConfig().moleclawMaxGroupSize);
            SpawnPlacements.register(UREntities.MOLECLAW_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Moleclaw::canDragonSpawn);
        }

        if (URConfig.getConfig().naturalRiverPikehornSpawn) {
            BiomeModifications.addSpawn(BiomeSelectors.tag(URTags.RIVER_PIKEHORN_SPAWN_BLACKLIST).negate(),
                    URMobCategory.SMALL_DRAGON.mobCategory,
                    UREntities.RIVER_PIKEHORN_ENTITY,
                    1,
                    URConfig.getConfig().riverPikehornMinGroupSize, URConfig.getConfig().riverPikehornMaxGroupSize);
            SpawnPlacements.register(UREntities.RIVER_PIKEHORN_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, URDragonEntity::canDragonSpawn);
        }

        if (URConfig.getConfig().naturalLightningChaserSpawn) {
            BiomeModifications.addSpawn(BiomeSelectors.tag(URTags.LIGHTNING_CHASER_SPAWN_BLACKLIST).negate(),
                    URMobCategory.DRAGON.mobCategory,
                    UREntities.LIGHTNING_CHASER_ENTITY,
                    1,
                    URConfig.getConfig().lightningChaserMinGroupSize, URConfig.getConfig().lightningChaserMaxGroupSize);
            SpawnPlacements.register(UREntities.LIGHTNING_CHASER_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LightningChaser::canDragonSpawn);
        }

        if (URConfig.getConfig().naturalMagmamuncherSpawn) {
            BiomeModifications.addSpawn(BiomeSelectors.tag(URTags.MAGMAMUNCHER_SPAWN_BLACKLIST).negate(),
                    URMobCategory.SMALL_DRAGON.mobCategory,
                    UREntities.MAGMAMUNCHER_ENTITY,
                    1,
                    URConfig.getConfig().magmamuncherMinGroupSize, URConfig.getConfig().magmamuncherMaxGroupSize);
            SpawnPlacements.register(UREntities.MAGMAMUNCHER_ENTITY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, URDragonEntity::canDragonSpawn);
        }
    }
}
