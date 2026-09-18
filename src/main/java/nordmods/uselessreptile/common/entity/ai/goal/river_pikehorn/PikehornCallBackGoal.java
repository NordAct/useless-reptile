package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.FlyingDragonCallBackGoal;

public class PikehornCallBackGoal extends FlyingDragonCallBackGoal<RiverPikehorn> {
    public PikehornCallBackGoal(RiverPikehorn entity) {
        super(entity);
    }

    @Override
    public boolean canUse() {
        if (mob.isHunting()) return false;
        return super.canUse();
    }
}
