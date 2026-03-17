package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.*;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class URDragonVariantTypes {
    public static final DragonVariantType<WyvernVariant> WYVERN = register(UREntities.WYVERN, new DragonVariantType<>(WyvernVariant.MAP_CODEC));
    public static final DragonVariantType<MoleclawVariant> MOLECLAW = register(UREntities.MOLECLAW, new DragonVariantType<>(MoleclawVariant.MAP_CODEC));
    public static final DragonVariantType<MagmamuncherVariant> MAGMAMUNCHER = register(UREntities.MAGMAMUNCHER, new DragonVariantType<>(MagmamuncherVariant.MAP_CODEC));
    public static final DragonVariantType<RiverPikehornVariant> RIVER_PIKEHORN = register(UREntities.RIVER_PIKEHORN, new DragonVariantType<>(RiverPikehornVariant.MAP_CODEC));
    public static final DragonVariantType<LightningChaserVariant> LIGHTNING_CHASER = register(UREntities.LIGHTNING_CHASER, new DragonVariantType<>(LightningChaserVariant.MAP_CODEC));

    public static <T extends DragonVariant> DragonVariantType<T> register(EntityType<? extends URDragonEntity> entityType, DragonVariantType<T> variantType) {
        return Registry.register(URRegistries.VARIANT_TYPE, BuiltInRegistries.ENTITY_TYPE.getKey(entityType), variantType);
    }

    public static void init() {}
}
