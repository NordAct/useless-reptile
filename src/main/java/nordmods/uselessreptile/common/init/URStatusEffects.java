package nordmods.uselessreptile.common.init;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.statuseffects.AcidStatusEffect;
import nordmods.uselessreptile.common.statuseffects.URStatusEffect;

public class URStatusEffects {

    public static final StatusEffect ACID = Registry.register(Registries.STATUS_EFFECT,
            new Identifier(UselessReptile.MODID, "acid"),
            new AcidStatusEffect());
    public static final StatusEffect SHOCK = Registry.register(Registries.STATUS_EFFECT,
            new Identifier(UselessReptile.MODID, "shock"),
            new URStatusEffect(StatusEffectCategory.HARMFUL, 12177894)
                    .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            "a3130519-0f1c-44ad-b0e7-e9d604d0fd10",
                            -0.5F, EntityAttributeModifier.Operation.MULTIPLY_BASE)
                    .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED,
                            "81416dd0-5e9a-4b89-a407-56e503b1bb52",
                            -0.5F, EntityAttributeModifier.Operation.MULTIPLY_BASE));

    public static void init() {
    }
}
