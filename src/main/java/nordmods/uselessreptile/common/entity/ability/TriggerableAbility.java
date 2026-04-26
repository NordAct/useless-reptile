package nordmods.uselessreptile.common.entity.ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public abstract class TriggerableAbility implements DragonAbility {
    private float cooldown;
    protected final CommonDragonAbilityData commonAbilityData;
    protected final TriggerableAbility.Data triggerableAbilityData;
    protected boolean wasTriggered;

    protected TriggerableAbility(CommonDragonAbilityData commonAbilityData, TriggerableAbility.Data triggerableAbilityData) {
        this.commonAbilityData = commonAbilityData;
        this.triggerableAbilityData = triggerableAbilityData;
    }

    @Override
    public CommonDragonAbilityData getCommonAbilityData() {
        return commonAbilityData;
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
        return cooldown >= getMaxCooldown() - getActiveTime();
    }

    @Override
    public void tickActive(URDragonEntity entity) {
        if (!wasTriggered && cooldown < getMaxCooldown() - getTriggerTime()) {
            wasTriggered = true;
            trigger(entity);
        }
    }

    @Override
    public void tick(URDragonEntity entity) {
        DragonAbility.super.tick(entity);
        if (!isActive(entity)) wasTriggered = false;
    }

    public float getTriggerTime() {
        return getTriggerableAbilityData().triggerTimeSeconds() * 20;
    }

    public float getActiveTime() {
        return getTriggerableAbilityData().activeTimeSeconds() * 20;
    }

    public TriggerableAbility.Data getTriggerableAbilityData() {
        return triggerableAbilityData;
    }

    public abstract void trigger(URDragonEntity entity);

    public record Data(float triggerTimeSeconds, float activeTimeSeconds) {
        public static final MapCodec<Data> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("trigger_time_seconds").forGetter(Data::triggerTimeSeconds),
                ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("active_time_seconds").forGetter(Data::activeTimeSeconds)
        ).apply(i, Data::new));

        public Data {
            if (triggerTimeSeconds > activeTimeSeconds)
                throw new IllegalStateException("Trigger time must be less than or equal to active time");
        }
    }
}
