package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.*;

public class URDragonAbilityTypes {
    public static final DragonAbilityType<MeleeAttackAbility> MELEE_ATTACK = register("melee_attack", new DragonAbilityType<>(MeleeAttackAbility.MAP_CODEC));
    public static final DragonAbilityType<ShotAttackAbility> SHOT_ATTACK = register("shot_attack", new DragonAbilityType<>(ShotAttackAbility.MAP_CODEC));
    public static final DragonAbilityType<ShockwaveAttackAbility> SHOCKWAVE_ATTACK = register("shockwave_attack", new DragonAbilityType<>(ShockwaveAttackAbility.MAP_CODEC));
    public static final DragonAbilityType<LightningBreathAttackAbility> LIGHTNING_BREATH_ATTACK = register("lightning_breath_attack", new DragonAbilityType<>(LightningBreathAttackAbility.MAP_CODEC));
    public static final DragonAbilityType<NoopAbility> NOOP_ABILITY = register("noop", new DragonAbilityType<>(NoopAbility.MAP_CODEC));
    public static final DragonAbilityType<BlockBreakingMeleeAttackAbility> BLOCK_BREAKING_MELEE_ATTACK_ABILITY = register("block_breaking_melee_attack", new DragonAbilityType<>(BlockBreakingMeleeAttackAbility.MAP_CODEC));

    public static <T extends DragonAbility> DragonAbilityType<T> register(Identifier id, DragonAbilityType<T> variantType) {
        return Registry.register(URRegistries.ABILITY_TYPE, id, variantType);
    }

    private static <T extends DragonAbility> DragonAbilityType<T> register(String id, DragonAbilityType<T> variantType) {
        return  register(UselessReptile.id(id), variantType);
    }

    public static void init() {}
}
