package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;

public record RiverPikehornVariant(CommonDragonVariantData common) implements DragonVariant {
    public static final MapCodec<RiverPikehornVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                CommonDragonVariantData.MAP_CODEC.forGetter(RiverPikehornVariant::common)
            ).apply(i, RiverPikehornVariant::new)
    );

    @Override
    public DragonVariantType<?> getType() {
        return URDragonVariantTypes.RIVER_PIKEHORN;
    }
}
