package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.FlyingDragonCallBackGoal;

public class RiverPikehornCallBackGoal extends FlyingDragonCallBackGoal<RiverPikehorn> {
    public RiverPikehornCallBackGoal(RiverPikehorn entity) {
        super(entity);
    }

    @Override
    public boolean canUse() {
        if (mob.isHunting()) return false;
        return super.canUse();
    }
}
