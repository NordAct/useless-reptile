package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.entity.Moleclaw;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record MoleclawUseCondition(
       boolean isPanicking
) implements UseCondition {
    public static final MapCodec<MoleclawUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("is_panicking").forGetter(MoleclawUseCondition::isPanicking)
    ).apply(i, MoleclawUseCondition::new));
    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.MOLECLAW;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof Moleclaw moleclaw) {
            return moleclaw.isPanicking() == isPanicking;
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on " + entity.getDragonId());
        }
    }
}
