package nordmods.uselessreptile.common.entity.ability;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public abstract class TriggerableAbility implements DragonAbility {
    private float cooldown;
    protected final CommonDragonAbilityData common;
    protected final float triggerTime;
    protected final float activeTime;
    protected boolean wasTriggered;

    protected TriggerableAbility(CommonDragonAbilityData common, float triggerTime, float activeTime) {
        if (triggerTime > activeTime)
            throw new  IllegalStateException("Trigger time must be less or equal than active time");
        this.common = common;
        this.triggerTime = triggerTime;
        this.activeTime = activeTime;
    }

    @Override
    public CommonDragonAbilityData common() {
        return common;
    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(URDragonEntity entity, float cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean isActive(URDragonEntity entity) {
        return cooldown >= getMaxCooldown() - activeTime;
    }

    @Override
    public void tickActive(URDragonEntity entity) {
        if (!wasTriggered && cooldown < getMaxCooldown() - triggerTime) {
            wasTriggered = true;
            trigger(entity);
        }
    }

    @Override
    public void tick(URDragonEntity entity) {
        DragonAbility.super.tick(entity);
        if (!isActive(entity)) wasTriggered = false;
    }

    public abstract void trigger(URDragonEntity entity);
}
