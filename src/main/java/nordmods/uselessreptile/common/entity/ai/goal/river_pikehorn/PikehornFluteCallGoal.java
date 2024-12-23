package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;

public class PikehornFluteCallGoal extends FlyingDragonCallBackGoal<RiverPikehornEntity> {

    public PikehornFluteCallGoal(RiverPikehornEntity entity) {
        super(entity);
    }

    @Override
    public boolean canStart() {
        if (entity.isSitting()) return false;
        return super.canStart();
    }

    @Override
    public void start() {
        super.start();
        entity.stopHunt();
    }
}
