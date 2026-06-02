package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

import java.util.Optional;

public record FlyingMovementUseCondition(
        Optional<Boolean> flying,
        Optional<FlyingDragon.TiltState> tiltState,
        Optional<Boolean> gliding
) implements UseCondition {
    public static final MapCodec<FlyingMovementUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("flying").forGetter(FlyingMovementUseCondition::flying),
            StringRepresentable.fromEnum(FlyingDragon.TiltState::values).optionalFieldOf("tilt_state").forGetter(FlyingMovementUseCondition::tiltState),
            Codec.BOOL.optionalFieldOf("gliding").forGetter(FlyingMovementUseCondition::gliding)
    ).apply(i, FlyingMovementUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.FLYING_MOVEMENT_CONDITION;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof FlyingDragon flyingDragon) {
            if (flying.isPresent() && flying.get() != flyingDragon.isFlying()) return false;
            if (tiltState.isPresent() && tiltState.get() != flyingDragon.getTiltState()) return false;
            return gliding.isEmpty() || gliding.get() == flyingDragon.isFlyGliding();
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on non FlyingDragon entity");
        }
    }
}
