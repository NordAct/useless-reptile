package nordmods.uselessreptile.common.init;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.item.FluteItem;

public class URRegistries {
    public static final Registry<DragonVariantType<?>> VARIANT_TYPE = new MappedRegistry<>(URResourceKeys.DRAGON_VARIANT_TYPE, Lifecycle.stable());
    public static final Registry<FluteItem.FluteMode> FLUTE_MODE = new MappedRegistry<>(URResourceKeys.FLUTE_MODE, Lifecycle.stable());

    public static void init() {}
}
