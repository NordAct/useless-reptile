package nordmods.uselessreptile.common.dragon_ability.holder;

import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonAbilityHolder {
    private final DragonAbility ability;
    private final URDragonEntity entity;
    private float cooldown;

    public DragonAbilityHolder(DragonAbility ability, URDragonEntity entity) {
        this.ability = ability;
        this.entity = entity;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public DragonAbility getAbility() {
        return ability;
    }

    public void tick() {
        ability.tick(this);
        if (getCooldown() > 0) {
            //Minecraft.getInstance().player.sendSystemMessage(Component
            //        .literal(getCooldown() + "(" + Minecraft.getInstance().player.tickCount + ")" + (entity.level().isClientSide() ? " - Client" : " - Server"))
            //        .withStyle(entity.level().isClientSide() ? ChatFormatting.AQUA : ChatFormatting.GOLD));
            setCooldown(Math.max(getCooldown() - ability.getCooldownRecoverySpeed(this), 0));
        }
    }

    public void use() {
        if (cooldown <= 0 && ability.canUse(this)) {
            setCooldown(ability.getMaxCooldown());
            ability.getCommonAbilityData().animations().forEach(a -> a.tryPlay(entity));
            entity.onAbilityActivated(ability);
        }
    }

    public URDragonEntity getEntity() {
        return entity;
    }
}
