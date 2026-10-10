package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import nordmods.uselessreptile.common.dragon_ability.type.DragonAbilityType;
import nordmods.uselessreptile.common.dragon_ability.use_condition.UseCondition;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;

public interface DragonAbility extends TypedInstance<DragonAbilityType<?>> {
    Codec<DragonAbility> CODEC = URRegistries.ABILITY_TYPE.byNameCodec()
            .dispatch("type", DragonAbility::getType, DragonAbilityType::codec);

    DragonAbilityType<?> getType();
    CommonDragonAbilityData getCommonAbilityData();
    DragonAbilityHolder createAbilityHolder(URDragonEntity entity);

    boolean isActive(DragonAbilityHolder holder);
    void tickActive(DragonAbilityHolder holder);

    default float getMaxCooldown() {
        return getCommonAbilityData().cooldownTimeSeconds() * 20;
    }

    default float getCooldownRecoverySpeed(DragonAbilityHolder holder) {
        return holder.getEntity().getCooldownModifier();
    }

    default void tick(DragonAbilityHolder holder) {
        if (isActive(holder)) tickActive(holder);
    }

    /// @param holder ability holder
    /// @return if ability can be used at current moment of time and in current use conditions
    default boolean canUse(DragonAbilityHolder holder) {
        if (isBlockedByOtherAbility(holder)) return false;
        if (holder.getEntity().hasControllingPassenger()) {
            if (!canUseControlled(holder)) return false;
        } else if (!canUseUncontrolled(holder)) return false;
        return canBeUsed(holder);
    }

    /// @param holder ability holder
    /// @return if ability can be used based on current use conditions
    default boolean canBeUsed(DragonAbilityHolder holder) {
        return UseCondition.testAll(getCommonAbilityData().conditions(), holder.getEntity());
    }

    default boolean canUseUncontrolled(DragonAbilityHolder holder) {
        return true;
    }

    default boolean canUseControlled(DragonAbilityHolder holder) {
        return true;
    }

    default boolean blockOtherAbilitiesIfActive() {
        return getCommonAbilityData().blockOtherAbilitiesIfActive();
    }

    default boolean isBlockedByOtherAbility(DragonAbilityHolder holder) {
        for (DragonAbilityHolder abilityHolder : holder.getEntity().getAbilityHolders().values()) {
            if (abilityHolder.getAbility().blockOtherAbilitiesIfActive() && abilityHolder.getAbility().isActive(abilityHolder)) return true;
        }
        return false;
    }

    @Override
    default Holder<DragonAbilityType<?>> typeHolder() {
        return getType().builtInRegistryHolder();
    }
}
