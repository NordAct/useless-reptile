package nordmods.uselessreptile.common.init;

import nordmods.uselessreptile.common.entity.ability.DragonAbilityType;
import nordmods.uselessreptile.common.entity.ability.MeleeAttackAbility;
import nordmods.uselessreptile.common.entity.ability.ShotAttackAbility;

public class URDragonAbilityTypes {
    public static final DragonAbilityType<MeleeAttackAbility> MELEE_ATTACK = new DragonAbilityType<>(MeleeAttackAbility.MAP_CODEC);
    public static final DragonAbilityType<ShotAttackAbility> SHOT_ATTACK = new DragonAbilityType<>(ShotAttackAbility.MAP_CODEC);

    public static void init() {}
}
