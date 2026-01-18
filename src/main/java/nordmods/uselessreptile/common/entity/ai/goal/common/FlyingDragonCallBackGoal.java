package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonCallBackGoal<T extends URDragonEntity & FlyingDragon> extends DragonCallBackGoal {
    protected final T entity;
    protected int ticksToStop;

    public FlyingDragonCallBackGoal(T entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void start() {
        super.start();
        ticksToStop = 0;
    }

    protected void checkProximity(double currentDistance) {
        if (!entity.isFlying() && owner.onGround()) {
            ticksToStop = 0;
            super.checkProximity(currentDistance);
            return;
        }
        if (currentDistance < proximityRange) {
            if (entity.isFlying()) {
                if (ticksToStop > 10) entity.shouldFollow = false;
                else ticksToStop++;
            } else entity.shouldFollow = false;
        } else ticksToStop = 0;
    }
}
