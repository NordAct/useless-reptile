package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonCallBackGoal<T extends URDragonEntity & FlyingDragon> extends DragonCallBackGoal {
    protected final T entity;

    public FlyingDragonCallBackGoal(T entity) {
        super(entity);
        this.entity = entity;
    }

    protected void checkProximity(double currentDistance) {
        if (!entity.isFlying()) ticksToStop = 0;
        double maxDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);
        if (currentDistance < maxDistance && (owner.isOnGround() || !entity.isFlying())) {
            if (entity.isFlying()) {
                if (ticksToStop > 10) entity.shouldFollow = false;
                else ticksToStop++;
            } else entity.shouldFollow = false;
        } else ticksToStop = 0;
    }
}
