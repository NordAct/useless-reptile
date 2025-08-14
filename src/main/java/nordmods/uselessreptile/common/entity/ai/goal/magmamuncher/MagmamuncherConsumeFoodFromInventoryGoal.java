package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonConsumeFoodFromInventoryGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class MagmamuncherConsumeFoodFromInventoryGoal extends DragonConsumeFoodFromInventoryGoal {
    public MagmamuncherConsumeFoodFromInventoryGoal(URDragonEntity dragon) {
        super(dragon);
    }

    @Override
    public boolean canStart() {
        return super.canStart() || isOwnerOnFire();
    }

    @Override
    protected void afterItemConsumed(ItemStack stack) {
        super.afterItemConsumed(stack);
        if (dragon.getOwner() == dragon.getVehicle() && dragon.getOwner() != null) {
            dragon.getOwner().addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.FIRE_RESISTANCE,
                            (int) (dragon.getWorld().getFuelRegistry().getFuelTicks(stack) * URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier)
                    )
            );
            URDragonEntity.SoundInfo info = dragon.getSoundInfo("apply_fire_resistance");
            if (info != null) dragon.getWorld().playSound(dragon, dragon.getX(), dragon.getY(), dragon.getZ(), SoundEvent.of(info.id()), dragon.getSoundCategory(), info.volume(), info.pitch());
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
            dragon.getServer().getPlayerManager().sendToAll(packet);
        }
    }

    @Override
    protected boolean canConsume() {
        return super.canConsume() || isOwnerOnFire();
    }

    private boolean isOwnerOnFire() {
        return dragon.getOwner() != null
                && dragon.getOwner() == dragon.getVehicle()
                && dragon.getOwner().getRecentDamageSource() != null
                && dragon.getOwner().getRecentDamageSource().isIn(DamageTypeTags.IS_FIRE)
                && !dragon.getOwner().hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }
}
