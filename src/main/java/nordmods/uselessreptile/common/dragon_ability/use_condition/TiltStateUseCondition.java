package nordmods.uselessreptile.common.dragon_ability.use_condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record TiltStateUseCondition(FlyingDragon.TiltState tiltState) implements UseCondition {
    public static final MapCodec<TiltStateUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            StringRepresentable.fromEnum(FlyingDragon.TiltState::values).fieldOf("tilt_state").forGetter(TiltStateUseCondition::tiltState)
    ).apply(i, TiltStateUseCondition::new));

    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.TILT_STATE;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof FlyingDragon flyingDragon) {
            return tiltState == flyingDragon.getTiltState();
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on non FlyingDragon entity");
        }
    }
}
