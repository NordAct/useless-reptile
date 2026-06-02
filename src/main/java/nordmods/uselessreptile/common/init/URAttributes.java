package nordmods.uselessreptile.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import nordmods.uselessreptile.UselessReptile;

public class URAttributes {
    public static final Holder<Attribute> DRAGON_GROUND_ROTATION_SPEED = register("dragon.ground_rotation_speed", 0, 180, 1, true, Attribute.Sentiment.POSITIVE);
    public static final Holder<Attribute> DRAGON_FLYING_ROTATION_SPEED = register("dragon.flying_rotation_speed", 0, 180, 1, true, Attribute.Sentiment.POSITIVE);
    public static final Holder<Attribute> DRAGON_PRIMARY_ATTACK_COOLDOWN = register("dragon.primary_attack_cooldown", 0, 2048, 20, true, Attribute.Sentiment.NEGATIVE);
    public static final Holder<Attribute> DRAGON_SECONDARY_ATTACK_COOLDOWN = register("dragon.secondary_attack_cooldown", 0, 2048, 20, true, Attribute.Sentiment.NEGATIVE);
    public static final Holder<Attribute> DRAGON_ACCELERATION_DURATION = register("dragon.acceleration_duration", 0, 2048, 1, true, Attribute.Sentiment.NEGATIVE);
    public static final Holder<Attribute> DRAGON_VERTICAL_SPEED = register("dragon.vertical_speed", 0, 1024, 0, true, Attribute.Sentiment.POSITIVE);
    public static final Holder<Attribute> DRAGON_MINING_LEVEL = register("dragon.mining_level", 0, 1024, 0, true, Attribute.Sentiment.POSITIVE);
    public static final Holder<Attribute> DRAGON_SPECIAL_ATTACK_COOLDOWN = register("dragon.special_attack_cooldown", 0, 2048, 20, true, Attribute.Sentiment.NEGATIVE);

    public static void init() {}

    private static Holder<Attribute> register(String id, float min, float max, float fallback, boolean tracked, Attribute.Sentiment category) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, UselessReptile.id(id), new RangedAttribute("attribute.name." + id, fallback, min, max).setSyncable(tracked).setSentiment(category));
    }
}
