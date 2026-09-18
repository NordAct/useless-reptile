package nordmods.uselessreptile.common.entity.ai.goal.common.flying;

import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonLookAtEntityGoal;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonLookAtEntityGoal<T extends URDragonEntity & FlyingDragon> extends DragonLookAtEntityGoal<T> {
    public FlyingDragonLookAtEntityGoal(T mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance);
    }

    @Override
    public boolean canUse() {
        if (mob.isFlying() && mob.getNavigation().isInProgress()) return false;
        return super.canUse();
    }
}
