package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;

public record LightningChaserVariant(CommonDragonVariantData common) implements DragonVariant {
    public static final MapCodec<LightningChaserVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                CommonDragonVariantData.MAP_CODEC.forGetter(LightningChaserVariant::common)
            ).apply(i, LightningChaserVariant::new)
    );

    @Override
    public DragonVariantType<?> getType() {
        return URDragonVariantTypes.LIGHTNING_CHASER;
    }
}
