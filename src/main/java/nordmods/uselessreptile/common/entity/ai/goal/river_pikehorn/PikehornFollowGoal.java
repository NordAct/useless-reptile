package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;

public class PikehornFollowGoal extends FlyingDragonCallBackGoal<RiverPikehorn> {
    private static final int TOLERANCE_DISTANCE_SQUARED = 256;

    public PikehornFollowGoal(RiverPikehorn entity) {
        super(entity);
    }

    @Override
    public boolean canUse() {
        if (entity.getTarget() != null || entity.forceTargetInWater) return false;
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;
        double distance = entity.distanceToSqr(owner);
        if (!entity.shouldFollow) {
            if (distance > TOLERANCE_DISTANCE_SQUARED && entity.getRandom().nextInt(TOLERANCE_DISTANCE_SQUARED) < distance - TOLERANCE_DISTANCE_SQUARED)
                entity.shouldFollow = true;
            else return false;
        }
        return super.canUse();
    }
}
