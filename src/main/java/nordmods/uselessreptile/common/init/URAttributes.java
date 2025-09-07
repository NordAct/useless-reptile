package nordmods.uselessreptile.common.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import nordmods.uselessreptile.UselessReptile;

public class URAttributes {
    public static final RegistryEntry<EntityAttribute> DRAGON_GROUND_ROTATION_SPEED = register("dragon.ground_rotation_speed", 0, 180, 1, true, EntityAttribute.Category.POSITIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_FLYING_ROTATION_SPEED = register("dragon.flying_rotation_speed", 0, 180, 1, true, EntityAttribute.Category.POSITIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_PRIMARY_ATTACK_COOLDOWN = register("dragon.primary_attack_cooldown", 0, 2048, 20, true, EntityAttribute.Category.NEGATIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_SECONDARY_ATTACK_COOLDOWN = register("dragon.secondary_attack_cooldown", 0, 2048, 20, true, EntityAttribute.Category.NEGATIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_ACCELERATION_DURATION = register("dragon.acceleration_duration", 0, 2048, 1, true, EntityAttribute.Category.NEGATIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_VERTICAL_SPEED = register("dragon.vertical_speed", 0, 1024, 0, true, EntityAttribute.Category.POSITIVE);
    public static final RegistryEntry<EntityAttribute> MOLECLAW_MINING_LEVEL = register("moleclaw.mining_level", 0, 1024, 0, true, EntityAttribute.Category.POSITIVE);
    public static final RegistryEntry<EntityAttribute> DRAGON_SPECIAL_ATTACK_COOLDOWN = register("dragon.special_attack_cooldown", 0, 2048, 20, true, EntityAttribute.Category.NEGATIVE);

    private static RegistryEntry<EntityAttribute> register(String id, float min, float max, float fallback, boolean tracked, EntityAttribute.Category category) {
        return Registry.registerReference(Registries.ATTRIBUTE, UselessReptile.id(id), new ClampedEntityAttribute("attribute.name." + id, fallback, min, max).setTracked(tracked).setCategory(category));
    }
}
