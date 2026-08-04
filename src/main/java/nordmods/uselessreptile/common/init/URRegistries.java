package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;
import nordmods.uselessreptile.common.dragon_ability.data.UseConditionType;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.item.FluteItem;

public class URRegistries {
    public static final Registry<DragonVariantType<?>> VARIANT_TYPE = FabricRegistryBuilder.create(URResourceKeys.DRAGON_VARIANT_TYPE).buildAndRegister();
    public static final Registry<FluteItem.FluteMode> FLUTE_MODE = FabricRegistryBuilder.create(URResourceKeys.FLUTE_MODE).buildAndRegister();
    public static final Registry<DragonAbilityType<?>> ABILITY_TYPE = FabricRegistryBuilder.create(URResourceKeys.DRAGON_ABILITY_TYPE).buildAndRegister();
    public static final Registry<UseConditionType<?>> USE_CONDITION_TYPE = FabricRegistryBuilder.create(URResourceKeys.USE_CONDITION_TYPE).buildAndRegister();

    public static void init() {}
}
