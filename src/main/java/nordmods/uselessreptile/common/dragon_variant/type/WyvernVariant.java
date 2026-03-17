package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;

public record WyvernVariant(CommonDragonVariantData common) implements DragonVariant {
    public static final MapCodec<WyvernVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                CommonDragonVariantData.MAP_CODEC.forGetter(WyvernVariant::common)
            ).apply(i, WyvernVariant::new)
    );

    @Override
    public DragonVariantType<?> getType() {
        return URDragonVariantTypes.WYVERN;
    }
}
