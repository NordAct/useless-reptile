package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;

public class PikehornFluteCallGoal extends FlyingDragonCallBackGoal<RiverPikehorn> {

    public PikehornFluteCallGoal(RiverPikehorn entity) {
        super(entity);
    }

    @Override
    public boolean canUse() {
        if (entity.isOrderedToSit()) return false;
        return super.canUse();
    }

    @Override
    public void start() {
        super.start();
        entity.stopHunt();
    }
}
