package nordmods.uselessreptile.common.mob_effect;

import eu.pb4.common.protection.api.CommonProtection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.common.init.URDamageTypes;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.network.URNetworkHelper;

public class AcidMobEffect extends URMobEffect {
    public AcidMobEffect() {
        super(MobEffectCategory.HARMFUL, 10085398);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}

    @Override
    public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {
        if (!CommonProtection.canDamageEntity(world, entity, CommonProtection.UNKNOWN, null)) return false;

        int armorUnequipped = 0;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) armorUnequipped++;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && entity.getItemBySlot(EquipmentSlot.BODY).isEmpty()) armorUnequipped++;
        if (entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) armorUnequipped++;
        if (entity.getItemBySlot(EquipmentSlot.FEET).isEmpty()) armorUnequipped++;

        if (entity.hurtServer(world, entity.damageSources().source(URDamageTypes.ACID), amplifier * (1 + armorUnequipped) / 3f)) {
            entity.hurtArmor(entity.damageSources().source(URDamageTypes.ACID), amplifier * (1 + armorUnequipped) * 2);
            URNetworkHelper.playSound(entity, URSoundEvent.ACID_BURN, SoundSource.AMBIENT, 1, 1, 5);
        }

        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        URNetworkHelper.playSound(entity, URSoundEvent.ACID_SPLASH, SoundSource.AMBIENT, 1 ,1, 1);
    }
}
