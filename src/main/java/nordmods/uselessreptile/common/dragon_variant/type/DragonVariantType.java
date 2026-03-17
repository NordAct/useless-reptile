package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;

public record DragonVariantType<T extends DragonVariant>(MapCodec<T> codec) {
}
