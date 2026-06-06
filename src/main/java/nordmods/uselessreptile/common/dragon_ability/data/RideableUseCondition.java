package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record RideableUseCondition(boolean controlledByRider) implements UseCondition{
    public static final MapCodec<RideableUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("controlled_by_rider").forGetter(RideableUseCondition::controlledByRider)
    ).apply(i, RideableUseCondition::new));
    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.RIDEABLE;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        if (entity instanceof URRideableDragonEntity rideableDragon) {
            return rideableDragon.hasControllingPassenger() == controlledByRider;
        } else {
            throw new IllegalStateException("Cannot use " + getType().getId() + " on non rideable dragon");
        }
    }
}
