package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;

import java.util.List;

public interface UseCondition {
    Codec<UseCondition> CODEC = URRegistries.USE_CONDITION_TYPE.byNameCodec()
            .dispatch("type", UseCondition::getType, UseConditionType::codec);

    UseConditionType<?> getType();

    boolean test(URDragonEntity entity);

    static boolean testAll(List<UseCondition> conditions, URDragonEntity entity) {
        if (!conditions.isEmpty()) {
            for (int i = 0; i < conditions.size(); i++) {
                if (!conditions.get(i).test(entity)) return false;
            }
        }
        return true;
    }
}
