package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.item.FluteItem;

public class URRegistries {
    public static final Registry<DragonVariantType<?>> VARIANT_TYPE = FabricRegistryBuilder.create(URResourceKeys.DRAGON_VARIANT_TYPE).buildAndRegister();
    public static final Registry<FluteItem.FluteMode> FLUTE_MODE = FabricRegistryBuilder.create(URResourceKeys.FLUTE_MODE).buildAndRegister();

    public static void init() {}
}
