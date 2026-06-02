package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;

public interface UseCondition {
    Codec<UseCondition> CODEC = URRegistries.USE_CONDITION_TYPE.byNameCodec()
            .dispatch("type", UseCondition::getType, UseConditionType::codec);

    UseConditionType<?> getType();

    boolean test(URDragonEntity entity);
}
