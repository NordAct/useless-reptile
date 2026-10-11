package nordmods.uselessreptile.common.dragon_ability.holder;

import nordmods.uselessreptile.common.dragon_ability.DragonAbility;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.ApiStatus;

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
        if (!entity.level().isClientSide() && getCooldown() > 0) {
            setCooldown(Math.max(getCooldown() - ability.getCooldownRecoverySpeed(this), 0));
        }
    }

    /// Sets off ability with some checks. Do not call this on client side unless you do not intend to sync it with server
    public void use() {
        if (cooldown <= 0 && ability.canUse(this)) useUnchecked();
    }

    @ApiStatus.Internal
    public void useUnchecked() {
        setCooldown(ability.getMaxCooldown());
        if (getEntity().getAnimationProcessor() == null || !getEntity().level().isClientSide()) //if there's processor, animations are sever authoritative anyway
            ability.getCommonAbilityData().animations().forEach(a -> a.tryPlay(entity));
        entity.onAbilityActivated(ability);
    }

    public URDragonEntity getEntity() {
        return entity;
    }
}
