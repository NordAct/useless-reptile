package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonWanderAroundGoal extends WaterAvoidingRandomStrollGoal {

    final URDragonEntity mob;

    public DragonWanderAroundGoal(URDragonEntity entity) {
        super(entity, 1);
        this.mob = entity;
    }

    @Override
    public boolean canUse() {
        if (mob.isDancing()) return false;
        if (mob.isOrderedToSit()) return false;
        if (mob instanceof FlyingDragon flyingDragon)
            if (flyingDragon.isFlying()) return false;
        return super.canUse();
    }
}
