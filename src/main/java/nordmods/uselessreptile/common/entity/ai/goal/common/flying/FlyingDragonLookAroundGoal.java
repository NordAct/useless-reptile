package nordmods.uselessreptile.common.entity.ai.goal.common.flying;

import nordmods.uselessreptile.common.entity.ai.goal.common.DragonLookAroundGoal;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonLookAroundGoal<T extends URDragonEntity & FlyingDragon> extends DragonLookAroundGoal<T> {
    public FlyingDragonLookAroundGoal(T mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        if (mob.isFlying() && mob.getNavigation().isInProgress()) return false;
        return super.canUse();
    }
}
