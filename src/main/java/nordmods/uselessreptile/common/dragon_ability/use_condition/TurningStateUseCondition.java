package nordmods.uselessreptile.common.dragon_ability.use_condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record TurningStateUseCondition(URDragonEntity.TurningState turningState) implements UseCondition{
    public static final MapCodec<TurningStateUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            StringRepresentable.fromEnum(URDragonEntity.TurningState::values).fieldOf("turning_state").forGetter(TurningStateUseCondition::turningState)
    ).apply(i, TurningStateUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.TURNING_STATE;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        return turningState == entity.getTurningState();
    }
}
