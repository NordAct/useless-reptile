package nordmods.uselessreptile.common.dragon_ability;

import com.mojang.serialization.MapCodec;
import nordmods.uselessreptile.common.dragon_ability.data.CommonDragonAbilityData;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.List;
import java.util.Optional;

public class NoopAbility implements DragonAbility{
    private final CommonDragonAbilityData commonDragonAbilityData = new CommonDragonAbilityData(
            0,
            false,
            List.of(),
            List.of(),
            Optional.empty()
            );

    public static final MapCodec<NoopAbility> MAP_CODEC = MapCodec.unit(new NoopAbility());
    @Override
    public DragonAbilityType<?> getType() {
        return URDragonAbilityTypes.NOOP_ABILITY;
    }

    @Override
    public CommonDragonAbilityData getCommonAbilityData() {
        return commonDragonAbilityData;
    }

    @Override
    public DragonAbilityHolder createAbilityHolder(URDragonEntity entity) {
        return new DragonAbilityHolder(this, entity);
    }

    @Override
    public float getMaxCooldown() {
        return 0;
    }

    @Override
    public float getCooldownRecoverySpeed(DragonAbilityHolder holder) {
        return 0;
    }

    @Override
    public boolean isActive(DragonAbilityHolder holder) {
        return false;
    }

    @Override
    public void tickActive(DragonAbilityHolder holder) {

    }

    @Override
    public boolean blockOtherAbilitiesIfActive() {
        return false;
    }

    @Override
    public boolean canUse(DragonAbilityHolder holder) {
        return false;
    }

    @Override
    public boolean canBeUsed(DragonAbilityHolder holder) {
        return false;
    }

    @Override
    public boolean canUseControlled(DragonAbilityHolder holder) {
        return false;
    }

    @Override
    public boolean canUseUncontrolled(DragonAbilityHolder holder) {
        return false;
    }

    @Override
    public void tick(DragonAbilityHolder holder) {}
}
