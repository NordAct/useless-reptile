package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.*;
import nordmods.uselessreptile.common.entity.misc.Placeholder;
import nordmods.uselessreptile.common.entity.projectile.AcidBlast;
import nordmods.uselessreptile.common.entity.projectile.LightningBreath;
import nordmods.uselessreptile.common.entity.projectile.ShockwaveSphere;
import nordmods.uselessreptile.common.util.URMobCategory;


public class UREntities {
    public static final EntityType<Wyvern> WYVERN =
            register("wyvern", getBuilder(URMobCategory.DRAGON.mobCategory, Wyvern::new, 1.8f, 2.95f));
    public static final EntityType<Moleclaw> MOLECLAW =
            register("moleclaw", getBuilder(URMobCategory.UNDERGROUND_DRAGON.mobCategory, Moleclaw::new, 2, 2.9f));
    public static final EntityType<RiverPikehorn> RIVER_PIKEHORN =
            register("river_pikehorn", getBuilder(URMobCategory.SMALL_DRAGON.mobCategory, RiverPikehorn::new, 0.8f, 0.7f));
    public static final EntityType<LightningChaser> LIGHTNING_CHASER =
            register("lightning_chaser", getBuilder(URMobCategory.DRAGON.mobCategory, LightningChaser::new, 2.95f, 2.95f));
    public static final EntityType<AcidBlast> ACID_BLAST =
            register("acid_blast", getBuilder(MobCategory.MISC, AcidBlast::new, 0.5f, 0.5f, true, false));
    public static final EntityType<ShockwaveSphere> SHOCKWAVE_SPHERE =
            register("shockwave_sphere", getBuilder(MobCategory.MISC, ShockwaveSphere::new, 1, 1, true, true));
    public static final EntityType<LightningBreath> LIGHTNING_BREATH =
            register("lightning_breath", getBuilder(MobCategory.MISC, LightningBreath::new, 1f, 1f, true, true));
    public static final EntityType<Magmamuncher> MAGMAMUNCHER =
            register("magmamuncher", getBuilder(URMobCategory.SMALL_DRAGON.mobCategory, Magmamuncher::new, 0.6f,0.35f, false, true));
    public static final EntityType<Entity> PLACEHOLDER =
            register("placeholder", EntityType.Builder.of(Placeholder::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(0));


    public static void init(){
        FabricDefaultAttributeRegistry.register(WYVERN, Wyvern.createWyvernAttributes());
        FabricDefaultAttributeRegistry.register(MOLECLAW, Moleclaw.createMoleclawAttributes());
        FabricDefaultAttributeRegistry.register(RIVER_PIKEHORN, RiverPikehorn.createPikehornAttributes());
        FabricDefaultAttributeRegistry.register(LIGHTNING_CHASER, LightningChaser.createLightningChaserAttributes());
        FabricDefaultAttributeRegistry.register(MAGMAMUNCHER, Magmamuncher.createMagmamuncherAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, UselessReptile.id(id), builder.build(ResourceKey.create(Registries.ENTITY_TYPE, UselessReptile.id(id))));
    }

    private static <T extends Entity> EntityType.Builder<T> getBuilder(MobCategory spawnGroup, EntityType.EntityFactory<T> entity, float width, float height, boolean disableSummon, boolean fireImmune) {
        EntityType.Builder<T> builder = EntityType.Builder.of(entity, spawnGroup).sized(width, height).canSpawnFarFromPlayer();
        if (disableSummon) builder.noSummon();
        if (fireImmune) builder.fireImmune();
        return builder;
    }

    private static <T extends Entity> EntityType.Builder<T> getBuilder(MobCategory spawnGroup, EntityType.EntityFactory<T> entity, float width, float height) {
        return getBuilder(spawnGroup, entity, width, height, false, false);
    }
}

