package nordmods.uselessreptile.common.dragon_variant.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;

import java.util.List;

public record RiverPikehornVariant(
        CommonDragonVariantData common,
        List<ExtraCodecs.TagOrElementLocation> huntTargets
        ) implements DragonVariant {
    public static final MapCodec<RiverPikehornVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonDragonVariantData.MAP_CODEC.forGetter(RiverPikehornVariant::common),
            ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("hunt_targets").forGetter(RiverPikehornVariant::huntTargets)
            ).apply(i, RiverPikehornVariant::new)
    );

    @Override
    public DragonVariantType<?> getType() {
        return URDragonVariantTypes.RIVER_PIKEHORN;
    }
}
