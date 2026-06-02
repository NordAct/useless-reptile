package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.Codec;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;

public interface DragonAbility {
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

    default boolean canUse(DragonAbilityHolder holder) {
        for (DragonAbilityHolder abilityHolder : holder.getEntity().getAbilityHolders().values()) {
            if (abilityHolder.getAbility().blockOtherAbilitiesIfActive() && abilityHolder.getAbility().isActive(holder)) return false;
        }
        return canBeUsed(holder);
    }

    default boolean canBeUsed(DragonAbilityHolder holder) {
        if (holder.getEntity().hasControllingPassenger()) {
            if (!canUseControlled(holder)) return false;
        } else if (!canUseUncontrolled(holder)) return false;
        return getCommonAbilityData().conditions().test(holder.getEntity());
    }

    default boolean canUseUncontrolled(DragonAbilityHolder holder) {
        return getCommonAbilityData().conditions().controlledByRider().isEmpty() || !getCommonAbilityData().conditions().controlledByRider().get();
    }

    default boolean canUseControlled(DragonAbilityHolder holder) {
        return getCommonAbilityData().conditions().controlledByRider().isEmpty() || getCommonAbilityData().conditions().controlledByRider().get();
    }

    default boolean blockOtherAbilitiesIfActive() {
        return getCommonAbilityData().blockOtherAbilitiesIfActive();
    }
}
