package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonConsumeItemFromInventoryGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class MagmamuncherApplyFireResistanceGoal extends DragonConsumeItemFromInventoryGoal {
    public MagmamuncherApplyFireResistanceGoal(URDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public boolean canStart() {
        return URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier > 0 && isOwnerOnFire();
    }

    @Override
    protected void beforeItemConsumed(ItemStack stack) {
        if (dragon.getOwner() == dragon.getVehicle() && dragon.getOwner() != null) {
            dragon.getOwner().addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.FIRE_RESISTANCE,
                            (int) (dragon.getWorld().getFuelRegistry().getFuelTicks(stack) * URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier)
                    )
            );
            URDragonEntity.SoundInfo info = dragon.getSoundInfo("apply_fire_resistance");
            if (info != null) dragon.getWorld().playSound(dragon, dragon.getX(), dragon.getY(), dragon.getZ(), SoundEvent.of(info.id()), dragon.getSoundCategory(), info.volume(), dragon.getRandom().nextTriangular(info.pitch(), info.pitchDeviation()));
            ParticleS2CPacket packet = new ParticleS2CPacket(
                    ParticleTypes.FLAME,
                    false,
                    false,
                    dragon.getX(),
                    dragon.getY(),
                    dragon.getZ(),
                    0.5f,
                    0.5f,
                    0.5f,
                    0,
                    10
            );
            if (dragon.getOwner() instanceof ServerPlayerEntity player) {
                URDragonEntity.grantTriggerableAdvancement(player, UselessReptile.id("dragon/magmamuncher_apply_fire_resistance"));
            }
            dragon.getServer().getPlayerManager().sendToAll(packet);
        }
    }

    @Override
    protected boolean canConsume() {
        return isOwnerOnFire();
    }

    private boolean isOwnerOnFire() {
        return dragon.getOwner() != null
                && dragon.getOwner() == dragon.getVehicle()
                && dragon.getOwner().getRecentDamageSource() != null
                && dragon.getOwner().getRecentDamageSource().isIn(DamageTypeTags.IS_FIRE)
                && !dragon.getOwner().hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }

    @Override
    protected boolean isConsumableItem(ItemStack stack) {
        return dragon.getWorld().getFuelRegistry().isFuel(stack);
    }
}
