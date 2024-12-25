package nordmods.uselessreptile.common.init;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.statuseffect.AcidStatusEffect;
import nordmods.uselessreptile.common.statuseffect.URStatusEffect;

public class URStatusEffects {

    public static final RegistryEntry<StatusEffect> ACID = Registry.registerReference(Registries.STATUS_EFFECT,
            UselessReptile.id("acid"),
            new AcidStatusEffect());
    public static final RegistryEntry<StatusEffect> SHOCK = Registry.registerReference(Registries.STATUS_EFFECT,
            UselessReptile.id("shock"),
            new URStatusEffect(StatusEffectCategory.HARMFUL, 12177894, ParticleTypes.ELECTRIC_SPARK)
                    .addAttributeModifier(EntityAttributes.MOVEMENT_SPEED,
                            UselessReptile.id("shock"),
                            -0.5F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(EntityAttributes.ATTACK_SPEED,
                            UselessReptile.id("shock"),
                            -0.5F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static void init() {
    }
}
