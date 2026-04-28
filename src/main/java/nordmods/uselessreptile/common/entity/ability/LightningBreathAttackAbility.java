package nordmods.uselessreptile.common.entity.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class LightningBreathAttackAbility extends ShotAttackAbility { //todo

    public LightningBreathAttackAbility(CommonDragonAbilityData commonAbilityData, Data triggerableAbilityData, EntityType<?> projectileEntityType, CompoundTag projectileEntityNbt, AnchorPoint anchorPoint) {
        super(commonAbilityData, triggerableAbilityData, projectileEntityType, projectileEntityNbt, anchorPoint, Optional.empty(), Vec3.ZERO, 1, 0, 0);
    }
}
