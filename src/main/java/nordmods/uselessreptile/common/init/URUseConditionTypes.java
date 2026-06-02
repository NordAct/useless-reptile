package nordmods.uselessreptile.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.dragon_ability.data.*;

public class URUseConditionTypes {
    public static final UseConditionType<MovementUseCondition> MOVEMENT_CONDITION = register("movement", new UseConditionType<>(MovementUseCondition.MAP_CODEC));
    public static final UseConditionType<FlyingMovementUseCondition> FLYING_MOVEMENT_CONDITION = register("flying_movement", new UseConditionType<>(FlyingMovementUseCondition.MAP_CODEC));
    public static final UseConditionType<RideableUseCondition> RIDEABLE_CONDITION = register("rideable", new UseConditionType<>(RideableUseCondition.MAP_CODEC));
    public static final UseConditionType<HeadMountUseCondition> HEAD_MOUNT_USE_CONDITION = register("head_mount", new UseConditionType<>(HeadMountUseCondition.MAP_CODEC));

    public static <T extends UseCondition> UseConditionType<T> register(Identifier id, UseConditionType<T> variantType) {
        return Registry.register(URRegistries.USE_CONDITION_TYPE, id, variantType);
    }

    private static <T extends UseCondition> UseConditionType<T> register(String id, UseConditionType<T> variantType) {
        return register(UselessReptile.id(id), variantType);
    }

    public static void init() {}
}
