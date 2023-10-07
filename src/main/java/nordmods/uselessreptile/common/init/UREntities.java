package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.WyvernProjectileEntity;


public class UREntities {

    public static final EntityType<WyvernEntity> WYVERN_ENTITY =
            register("wyvern", getBuilder(SpawnGroup.CREATURE, WyvernEntity::new, 1, 1));
    public static final EntityType<WyvernProjectileEntity> WYVERN_PROJECTILE_ENTITY =
            register("wyvern_projectile", getBuilder(SpawnGroup.MISC, WyvernProjectileEntity::new, 0.5f, 0.5f, true));
    public static final EntityType<MoleclawEntity> MOLECLAW_ENTITY =
            register("moleclaw", getBuilder(SpawnGroup.CREATURE, MoleclawEntity::new, MoleclawEntity.defaultWidth, MoleclawEntity.defaultHeight));
    public static final EntityType<RiverPikehornEntity> RIVER_PIKEHORN_ENTITY =
            register("river_pikehorn", getBuilder(SpawnGroup.CREATURE, RiverPikehornEntity::new, 1, 1));

    public static void init(){
        FabricDefaultAttributeRegistry.register(WYVERN_ENTITY, WyvernEntity.createWyvernAttributes());
        FabricDefaultAttributeRegistry.register(MOLECLAW_ENTITY, MoleclawEntity.createMoleclawAttributes());
        FabricDefaultAttributeRegistry.register(RIVER_PIKEHORN_ENTITY, RiverPikehornEntity.createPikehornAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String id, FabricEntityTypeBuilder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(UselessReptile.MODID, id), builder.build());
    }

    private static <T extends Entity> FabricEntityTypeBuilder<T> getBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> entity, float width, float height, boolean disableSummon) {
        FabricEntityTypeBuilder<T> builder = FabricEntityTypeBuilder.create(spawnGroup, entity).dimensions(EntityDimensions.changing(width, height));
        return disableSummon ? builder.disableSummon() : builder;
    }

    private static <T extends Entity> FabricEntityTypeBuilder<T> getBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> entity, float width, float height) {
        return getBuilder(spawnGroup, entity, width, height, false);
    }
}

