package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.dragon_ability.holder.TriggerableAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public abstract class TriggerableAbility implements DragonAbility {
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
    public boolean isActive(DragonAbilityHolder holder) {
        return holder.getCooldown() >= getMaxCooldown() - getActiveTime();
    }

    @Override
    public void tickActive(DragonAbilityHolder holder) {
        if (!(holder instanceof TriggerableAbilityHolder triggerableAbilityHolder)) return;
        if (!triggerableAbilityHolder.wasTriggered() && holder.getCooldown() <= getMaxCooldown() - getTriggerTime()) {
            triggerableAbilityHolder.setWasTriggered(true);
            trigger(holder);
        }
    }

    @Override
    public void tick(DragonAbilityHolder holder) {
        DragonAbility.super.tick(holder);
        if (holder instanceof TriggerableAbilityHolder triggerableAbilityHolder && triggerableAbilityHolder.getCooldown() <= 0) {
            triggerableAbilityHolder.setWasTriggered(false);
        }
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

    public abstract void trigger(DragonAbilityHolder holder);

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

    @Override
    public DragonAbilityHolder createAbilityHolder(URDragonEntity entity) {
        return new TriggerableAbilityHolder(this, entity);
    }

    @Override
    public boolean canUse(DragonAbilityHolder holder) {
        if (((TriggerableAbilityHolder)holder).wasTriggered()) return false;
        return DragonAbility.super.canUse(holder);
    }
}
