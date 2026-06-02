package nordmods.uselessreptile.common.init;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import nordmods.uselessreptile.common.dragon_ability.data.UseConditionType;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;

public class URRegistries {
    public static final Registry<DragonVariantType<?>> VARIANT_TYPE = new MappedRegistry<>(URResourceKeys.DRAGON_VARIANT_TYPE, Lifecycle.stable());
    public static final Registry<DragonAbilityType<?>> ABILITY_TYPE = new MappedRegistry<>(URResourceKeys.DRAGON_ABILITY_TYPE, Lifecycle.stable());
    public static final Registry<UseConditionType<?>> USE_CONDITION_TYPE = new MappedRegistry<>(URResourceKeys.USE_CONDITION_TYPE, Lifecycle.stable());
}
