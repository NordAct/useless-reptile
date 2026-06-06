package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.data.*;

public class URUseConditionTypes {
    public static final UseConditionType<MovementUseCondition> MOVEMENT = register("movement", new UseConditionType<>(MovementUseCondition.MAP_CODEC));
    public static final UseConditionType<FlyingMovementUseCondition> FLYING_MOVEMENT = register("flying_movement", new UseConditionType<>(FlyingMovementUseCondition.MAP_CODEC));
    public static final UseConditionType<RideableUseCondition> RIDEABLE = register("rideable", new UseConditionType<>(RideableUseCondition.MAP_CODEC));
    public static final UseConditionType<HeadMountUseCondition> HEAD_MOUNT = register("head_mount", new UseConditionType<>(HeadMountUseCondition.MAP_CODEC));
    public static final UseConditionType<MoleclawUseCondition> MOLECLAW = register("moleclaw", new UseConditionType<>(MoleclawUseCondition.MAP_CODEC));
    public static final UseConditionType<HasAbilityActiveUseCondition> HAS_ABILITY_ACTIVE = register("has_ability_active", new UseConditionType<>(HasAbilityActiveUseCondition.MAP_CODEC));

    public static <T extends UseCondition> UseConditionType<T> register(Identifier id, UseConditionType<T> useConditionType) {
        return Registry.register(URRegistries.USE_CONDITION_TYPE, id, useConditionType);
    }

    private static <T extends UseCondition> UseConditionType<T> register(String id, UseConditionType<T> useConditionType) {
        return register(UselessReptile.id(id), useConditionType);
    }

    public static void init() {}
}
