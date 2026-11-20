package nordmods.uselessreptile.common.dragon_ability.use_condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record FlyingUseCondition(boolean flying) implements UseCondition {
    public static final MapCodec<FlyingUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("flying").forGetter(FlyingUseCondition::flying)
    ).apply(i, FlyingUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.FLYING;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof FlyingDragon flyingDragon) {
            return flying == flyingDragon.isFlying();
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on non FlyingDragon entity");
        }
    }
}
