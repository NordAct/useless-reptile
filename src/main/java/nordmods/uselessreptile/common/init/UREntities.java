package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import nordmods.uselessreptile.common.util.URSpawnGroup;


public class UREntities {

    public static final EntityType<WyvernEntity> WYVERN_ENTITY =
            register("wyvern", getBuilder(URSpawnGroup.DRAGON.spawnGroup, WyvernEntity::new, 1, 1));
    public static final EntityType<MoleclawEntity> MOLECLAW_ENTITY =
            register("moleclaw", getBuilder(URSpawnGroup.UNDERGROUND_DRAGON.spawnGroup, MoleclawEntity::new, MoleclawEntity.defaultWidth, MoleclawEntity.defaultHeight));
    public static final EntityType<RiverPikehornEntity> RIVER_PIKEHORN_ENTITY =
            register("river_pikehorn", getBuilder(URSpawnGroup.SMALL_DRAGON.spawnGroup, RiverPikehornEntity::new, 1, 1));
    public static final EntityType<LightningChaserEntity> LIGHTNING_CHASER_ENTITY =
            register("lightning_chaser", getBuilder(URSpawnGroup.DRAGON.spawnGroup, LightningChaserEntity::new, 1, 1));
    public static final EntityType<AcidBlastEntity> ACID_BLAST_ENTITY =
            register("acid_blast", getBuilder(SpawnGroup.MISC, AcidBlastEntity::new, 0.5f, 0.5f, true, false));
    public static final EntityType<ShockwaveSphereEntity> SHOCKWAVE_SPHERE_ENTITY =
            register("shockwave_sphere", getBuilder(SpawnGroup.MISC, ShockwaveSphereEntity::new, 1, 1, true, true));
    public static final EntityType<LightningBreathEntity> LIGHTNING_BREATH_ENTITY =
            register("lightning_breath", getBuilder(SpawnGroup.MISC, LightningBreathEntity::new, 1f, 1f, true, true));


    public static void init(){
        FabricDefaultAttributeRegistry.register(WYVERN_ENTITY, WyvernEntity.createWyvernAttributes());
        FabricDefaultAttributeRegistry.register(MOLECLAW_ENTITY, MoleclawEntity.createMoleclawAttributes());
        FabricDefaultAttributeRegistry.register(RIVER_PIKEHORN_ENTITY, RiverPikehornEntity.createPikehornAttributes());
        FabricDefaultAttributeRegistry.register(LIGHTNING_CHASER_ENTITY, LightningChaserEntity.createLightningChaserAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, UselessReptile.id(id), builder.build());
    }

    private static <T extends Entity> EntityType.Builder<T> getBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> entity, float width, float height, boolean disableSummon, boolean fireImmune) {
        EntityType.Builder<T> builder = EntityType.Builder.create(entity, spawnGroup).dimensions(width, height).spawnableFarFromPlayer();
        if (disableSummon) builder.disableSummon();
        if (fireImmune) builder.makeFireImmune();
        return builder;
    }

    private static <T extends Entity> EntityType.Builder<T> getBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> entity, float width, float height) {
        return getBuilder(spawnGroup, entity, width, height, false, false);
    }
}

