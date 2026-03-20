package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.Util;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URRegistries;

public record DragonVariantType<T extends DragonVariant>(MapCodec<T> codec) {
    public String getTranslationKey() {
        return Util.makeDescriptionId("entity", URRegistries.VARIANT_TYPE.getKey(this));
    }
}
