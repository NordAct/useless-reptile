package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URRegistries;
import org.jspecify.annotations.Nullable;

public record DragonVariantType<T extends DragonVariant>(MapCodec<T> codec) {
    public String getTranslationKey() {
        return Util.makeDescriptionId("entity", URRegistries.VARIANT_TYPE.getKey(this));
    }

    public Identifier getId() {
        return URRegistries.VARIANT_TYPE.getKey(this);
    }

    @Nullable
    public static DragonVariantType<?> fromId(Identifier id) {
        return URRegistries.VARIANT_TYPE.getValue(id);
    }
}
