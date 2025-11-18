package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonConsumeItemFromInventoryGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class MagmamuncherApplyFireResistanceGoal extends DragonConsumeItemFromInventoryGoal {
    public MagmamuncherApplyFireResistanceGoal(URDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public boolean canUse() {
        return URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier > 0 && isOwnerOnFire();
    }

    @Override
    protected void beforeItemConsumed(ItemStack stack) {
        if (dragon.getOwner() == dragon.getVehicle() && dragon.getOwner() != null) {
            dragon.getOwner().addEffect(
                    new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,
                            (int) (dragon.level().fuelValues().burnDuration(stack) * URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier)
                    )
            );
            URDragonEntity.SoundInfo info = dragon.getSoundInfo("apply_fire_resistance");
            if (info != null) dragon.level().playSound(dragon, dragon.getX(), dragon.getY(), dragon.getZ(), SoundEvent.createVariableRangeEvent(info.id()), dragon.getSoundSource(), info.volume(), dragon.getRandom().triangle(info.pitch(), info.pitchDeviation()));
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
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
            if (dragon.getOwner() instanceof ServerPlayer player) {
                URDragonEntity.grantTriggerableAdvancement(player, UselessReptile.id("dragon/magmamuncher_apply_fire_resistance"));
            }
            dragon.level().getServer().getPlayerList().broadcastAll(packet);
        }
    }

    @Override
    protected boolean canConsume() {
        return isOwnerOnFire();
    }

    private boolean isOwnerOnFire() {
        return dragon.getOwner() != null
                && dragon.getOwner() == dragon.getVehicle()
                && dragon.getOwner().getLastDamageSource() != null
                && dragon.getOwner().getLastDamageSource().is(DamageTypeTags.IS_FIRE)
                && !dragon.getOwner().hasEffect(MobEffects.FIRE_RESISTANCE);
    }

    @Override
    protected boolean isConsumableItem(ItemStack stack) {
        return dragon.level().fuelValues().isFuel(stack);
    }
}
