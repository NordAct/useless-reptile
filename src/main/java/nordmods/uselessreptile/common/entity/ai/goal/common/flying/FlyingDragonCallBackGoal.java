package nordmods.uselessreptile.common.entity.ai.goal.common.flying;

import nordmods.uselessreptile.common.entity.ai.goal.common.DragonCallBackGoal;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class FlyingDragonCallBackGoal<T extends URDragonEntity & FlyingDragon> extends DragonCallBackGoal<T> {
    protected int ticksToStop;

    public FlyingDragonCallBackGoal(T mob) {
        super(mob);
    }

    @Override
    public void start() {
        super.start();
        ticksToStop = 0;
    }

    protected void checkProximity(double currentDistance) {
        if (!mob.isFlying() && owner.onGround()) {
            ticksToStop = 0;
            super.checkProximity(currentDistance);
            return;
        }
        if (currentDistance < proximityRange) {
            if (mob.isFlying()) {
                if (ticksToStop > 10) mob.shouldFollow = false;
                else ticksToStop++;
            } else mob.shouldFollow = false;
        } else ticksToStop = 0;
    }
}
