package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.WyvernProjectileEntity;


public class UREntities {

    public static final EntityType<WyvernEntity> WYVERN_ENTITY = register("wyvern", WyvernEntity::new, 1, 1, SpawnGroup.CREATURE);
    public static final EntityType<WyvernProjectileEntity> WYVERN_PROJECTILE_ENTITY = register("wyvern_projectile" ,WyvernProjectileEntity::new, 0.5F, 0.5F, SpawnGroup.MISC);
    public static final EntityType<MoleclawEntity> MOLECLAW_ENTITY = register("moleclaw", MoleclawEntity::new, MoleclawEntity.defaultWidth, MoleclawEntity.defaultHeight, SpawnGroup.CREATURE);
    public static final EntityType<RiverPikehornEntity> RIVER_PIKEHORN_ENTITY = register("river_pikehorn", RiverPikehornEntity::new, 1, 1, SpawnGroup.CREATURE);

    public static void init(){
        FabricDefaultAttributeRegistry.register(WYVERN_ENTITY, WyvernEntity.createWyvernAttributes());
        FabricDefaultAttributeRegistry.register(MOLECLAW_ENTITY, MoleclawEntity.createMoleclawAttributes());
        FabricDefaultAttributeRegistry.register(RIVER_PIKEHORN_ENTITY, RiverPikehornEntity.createPikehornAttributes());
    }


    public static <T extends Entity> EntityType<T> register(String id, EntityType.EntityFactory<T> entity, float width, float height, SpawnGroup group) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(UselessReptile.MODID, id),
                FabricEntityTypeBuilder.create(group, entity).dimensions(EntityDimensions.changing(width, height)).build()
        );
    }
}

