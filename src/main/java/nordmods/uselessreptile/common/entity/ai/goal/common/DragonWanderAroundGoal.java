package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;

public class DragonWanderAroundGoal extends WanderAroundFarGoal {

    final URDragonEntity mob;

    public DragonWanderAroundGoal(URDragonEntity entity) {
        super(entity, 1);
        this.mob = entity;
    }

    @Override
    public boolean canStart() {
        if (this.mob.isDancing()) return false;
        if (this.mob instanceof URRideableFlyingDragonEntity flyingDragon)
            if (flyingDragon.isFlying()) return false;
        return super.canStart();
    }
}
