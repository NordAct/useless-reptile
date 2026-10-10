package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import nordmods.uselessreptile.common.entity.RiverPikehorn;

public class RiverPikehornHuntTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final RiverPikehorn mob;
    public RiverPikehornHuntTargetGoal(RiverPikehorn mob) {
        super(mob, LivingEntity.class, true);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (!mob.isHunting() || mob.getPickupTarget() != null || !mob.forceTargetInWater || !mob.getOffhandItem().isEmpty()) return false;
        if (super.canUse()) return mob.isHuntingTarget(target);
        return false;
    }
}
