package nordmods.uselessreptile.common.init;

import nordmods.uselessreptile.common.entity.ability.DragonAbilityType;
import nordmods.uselessreptile.common.entity.ability.MeleeAttackAbility;

public class URDragonAbilityTypes {
    public static final DragonAbilityType<MeleeAttackAbility> MELEE_ATTACK = new DragonAbilityType<>(MeleeAttackAbility.MAP_CODEC);

    public static void init() {}
}
