package nordmods.uselessreptile.common.entity.ability;

import com.mojang.serialization.Codec;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistries;

public interface DragonAbility {
    Codec<DragonAbility> CODEC = URRegistries.ABILITY_TYPE.byNameCodec()
            .dispatch("type", DragonAbility::getType, DragonAbilityType::codec);

    DragonAbilityType<?> getType();

    CommonDragonAbilityData common();

    float getCooldown();
    void setCooldown(URDragonEntity entity, float cooldown);
    boolean isActive(URDragonEntity entity);

    default float getMaxCooldown() {
        return common().cooldownSeconds() * 20;
    }

    default float getCooldownRecoverySpeed(URDragonEntity entity) {
        return entity.getCooldownModifier();
    }

    default void tickCooldown(URDragonEntity entity) {
        if (getCooldown() > 0) setCooldown(entity, Math.max(getCooldown() - getCooldownRecoverySpeed(entity), 0));
    }

    default void tickActive(URDragonEntity entity) {}

    default void tick(URDragonEntity entity) {
        if (isActive(entity)) tickActive(entity);
        tickCooldown(entity);
    }

    default void use(URDragonEntity entity) {
        if (canUse(entity)) setCooldown(entity, getMaxCooldown());
    }

    default boolean canUse(URDragonEntity entity) {
        if (getCooldown() > 0) return false;
        for (DragonAbility ability : entity.getAbilityStorage().values()) {
            if (ability.blockOtherAbilitiesIfActive() && ability.isActive(entity)) return false;
        }
        if (entity.hasControllingPassenger()) {
            if (!canUseControlled(entity)) return false;
        } else if (!canUseUncontrolled(entity)) return false;
        return true;
    }

    default boolean canUseUncontrolled(URDragonEntity entity) {
        return true;
    }

    default boolean canUseControlled(URDragonEntity entity) {
        return true;
    }

    default boolean blockOtherAbilitiesIfActive() {
        return common().blockOtherAbilitiesIfActive();
    }
}
