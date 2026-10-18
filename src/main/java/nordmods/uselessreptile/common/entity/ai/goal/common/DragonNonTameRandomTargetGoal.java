package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonNonTameRandomTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final URDragonEntity mob;
    public DragonNonTameRandomTargetGoal(URDragonEntity mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
        this.mob = mob;
    }

    public DragonNonTameRandomTargetGoal(URDragonEntity mob, Class<T> targetType, boolean mustSee, TargetingConditions.Selector selector) {
        super(mob, targetType, 10, mustSee, false, selector);
        this.mob = mob;
    }

    public DragonNonTameRandomTargetGoal(URDragonEntity mob, Class<T> targetType, boolean mustSee, boolean mustReach) {
        super(mob, targetType, 10, mustSee, mustReach, null);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return mob.canGrief() && super.canUse();
    }
}
