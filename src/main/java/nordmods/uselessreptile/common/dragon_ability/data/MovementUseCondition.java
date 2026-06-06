package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

import java.util.Optional;

public record MovementUseCondition(
        Optional<Boolean> moving,
        Optional<Boolean> movingBackwards,
        Optional<URDragonEntity.TurningState> turningState
) implements UseCondition{
    public static final MapCodec<MovementUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("moving").forGetter(MovementUseCondition::moving),
            Codec.BOOL.optionalFieldOf("moving_backwards").forGetter(MovementUseCondition::movingBackwards),
            StringRepresentable.fromEnum(URDragonEntity.TurningState::values).optionalFieldOf("turning_state").forGetter(MovementUseCondition::turningState)
    ).apply(i, MovementUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.MOVEMENT;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (moving.isPresent() && moving.get() != entity.isMoving()) return false;
        if (movingBackwards.isPresent() && movingBackwards.get() != entity.isMovingBackwards()) return false;
        return turningState.isEmpty() || turningState.get() == entity.getTurningState();
    }
}
