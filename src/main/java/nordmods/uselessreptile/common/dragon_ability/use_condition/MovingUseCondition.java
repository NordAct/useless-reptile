package nordmods.uselessreptile.common.dragon_ability.use_condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record MovingUseCondition(boolean moving, boolean backwards) implements UseCondition{
    public static final MapCodec<MovingUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("moving").forGetter(MovingUseCondition::moving),
            Codec.BOOL.fieldOf("backwards").forGetter(MovingUseCondition::moving)
    ).apply(i, MovingUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.MOVING;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        return moving == entity.isMoving();
    }
}
