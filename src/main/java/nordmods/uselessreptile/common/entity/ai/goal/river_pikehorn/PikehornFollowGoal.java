package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.FlyingDragonCallBackGoal;

public class PikehornFollowGoal extends FlyingDragonCallBackGoal<RiverPikehornEntity> {


    public PikehornFollowGoal(RiverPikehornEntity entity) {
        super(entity);
    }

    @Override
    public boolean canStart() {
        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;
        if (entity.isLeashed() || entity.hasVehicle() || entity.isSitting()) return false;
        if (entity.getTarget() != null || entity.forceTargetInWater) return false;
        if (isFollowing) return true;
        double distance = entity.squaredDistanceTo(owner);

        return distance > 1024 && entity.getRandom().nextInt(1024) < distance - 1024;
    }
}
