package nordmods.uselessreptile.common.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.mob_effect.AcidMobEffect;

public class URMobEffect {

    public static final Holder<MobEffect> ACID = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
            UselessReptile.id("acid"),
            new AcidMobEffect());
    public static final Holder<MobEffect> SHOCK = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
            UselessReptile.id("shock"),
            new nordmods.uselessreptile.common.mob_effect.URMobEffect(MobEffectCategory.HARMFUL, 12177894, ParticleTypes.ELECTRIC_SPARK)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            UselessReptile.id("shock"),
                            -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.ATTACK_SPEED,
                            UselessReptile.id("shock"),
                            -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static void init() {
    }
}
