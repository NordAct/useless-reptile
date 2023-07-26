package nordmods.uselessreptile.common.statuseffects;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import nordmods.uselessreptile.common.init.URDamageSources;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.network.URPacketManager;

public class AcidStatusEffect extends StatusEffect {
    public AcidStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 10085398);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        int armorUnequipped = 0;
        if (entity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) armorUnequipped++;
        if (entity.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) armorUnequipped++;
        if (entity.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) armorUnequipped++;
        if (entity.getEquippedStack(EquipmentSlot.FEET).isEmpty()) armorUnequipped++;
        if (entity.getWorld().getTickOrder() % 10 == 0) {
            entity.damage(URDamageSources.ACID, (float) (amplifier * (1 + armorUnequipped)) / 3);
            URPacketManager.playSound(entity, URSounds.ACID_BURN, SoundCategory.AMBIENT, 1, 1, 5);
        }
        if (entity.getWorld().getTickOrder() % 20 == 0) entity.damageArmor(URDamageSources.ACID, amplifier * (1 + armorUnequipped) * 2);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        URPacketManager.playSound(entity, URSounds.ACID_SPLASH, SoundCategory.AMBIENT, 1 ,1, 1);
    }
}
