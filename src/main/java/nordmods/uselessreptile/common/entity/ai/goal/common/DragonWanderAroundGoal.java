package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonWanderAroundGoal extends WanderAroundFarGoal {

    final URDragonEntity mob;

    public DragonWanderAroundGoal(URDragonEntity entity) {
        super(entity, 1);
        this.mob = entity;
    }

    @Override
    public boolean canStart() {
        if (mob.isDancing()) return false;
        if (mob.isSitting()) return false;
        if (mob instanceof FlyingDragon flyingDragon)
            if (flyingDragon.isFlying()) return false;
        return super.canStart();
    }
}
