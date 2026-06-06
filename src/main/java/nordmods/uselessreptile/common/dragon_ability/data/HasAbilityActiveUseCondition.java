package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.common.dragon_ability.DragonAbilityType;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;
import nordmods.uselessreptile.common.init.URUseConditionTypes;

public record HasAbilityActiveUseCondition(DragonAbilityType<?> activeAbilityType) implements UseCondition {
    public static final MapCodec<HasAbilityActiveUseCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            URRegistries.ABILITY_TYPE.byNameCodec().fieldOf("active_ability_type").forGetter(HasAbilityActiveUseCondition::activeAbilityType)
    ).apply(i, HasAbilityActiveUseCondition::new));
    @Override
    public UseConditionType<?> getType() {
        return URUseConditionTypes.HAS_ABILITY_ACTIVE;
    }

    @Override
    public boolean test(URDragonEntity entity) {
        return entity.getAbilityHolders().values().stream().anyMatch(a -> a.getAbility().getType() == activeAbilityType && a.getAbility().isActive(a));
    }
}
