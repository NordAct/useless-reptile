package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.Magmamuncher;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonConsumeItemFromInventoryGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;

public class MagmamuncherApplyFireResistanceGoal extends DragonConsumeItemFromInventoryGoal<Magmamuncher> {
    public MagmamuncherApplyFireResistanceGoal(Magmamuncher dragon) {
        super(dragon);
    }

    @Override
    public boolean canUse() {
        return URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier > 0 && isOwnerOnFire();
    }

    @Override
    protected void beforeItemConsumed(ItemStack stack) {
        if (mob.level() instanceof ServerLevel level && mob.getOwner() == mob.getVehicle() && mob.getOwner() != null) {
            LootContext lootContext = new LootContext.Builder(
                    new LootParams.Builder(level)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(mob.blockPosition()))
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, mob)
                            .create(LootContextParamSets.SELECTOR)
            ).create(Optional.empty());
            mob.getOwner().addEffect(
                    new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,
                            (int) (stack.get(DataComponents.COOKING_FUEL).burnTime().get(lootContext , 0) * URConfig.getConfig().magmamuncherFireResistanceTimeMultiplier)
                    )
            );
            URDragonEntity.SoundInfo info = mob.getSoundInfo("apply_fire_resistance");
            if (info != null) mob.level().playSound(mob, mob.getX(), mob.getY(), mob.getZ(), SoundEvent.createVariableRangeEvent(info.id()), mob.getSoundSource(), info.volume(), mob.getRandom().triangle(info.pitch(), info.pitchDeviation()));
            ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                    ParticleTypes.FLAME,
                    false,
                    false,
                    mob.getX(),
                    mob.getY(),
                    mob.getZ(),
                    0.5f,
                    0.5f,
                    0.5f,
                    0,
                    10
            );
            if (mob.getOwner() instanceof ServerPlayer player) {
                URDragonEntity.grantTriggerableAdvancement(player, UselessReptile.id("dragon/magmamuncher_apply_fire_resistance"));
            }
            mob.level().getServer().getPlayerList().broadcastAll(packet);
        }
    }

    @Override
    protected boolean canConsume() {
        return isOwnerOnFire();
    }

    private boolean isOwnerOnFire() {
        return mob.getOwner() != null
                && mob.getOwner() == mob.getVehicle()
                && mob.getOwner().getLastDamageSource() != null
                && mob.getOwner().getLastDamageSource().is(DamageTypeTags.IS_FIRE)
                && !mob.getOwner().hasEffect(MobEffects.FIRE_RESISTANCE);
    }

    @Override
    protected boolean isConsumableItem(ItemStack stack) {
        return stack.get(DataComponents.COOKING_FUEL) != null;
    }
}
