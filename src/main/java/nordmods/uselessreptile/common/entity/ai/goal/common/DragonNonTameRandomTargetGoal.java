package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

public class DragonNonTameRandomTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final URDragonEntity mob;
    public DragonNonTameRandomTargetGoal(URDragonEntity mob, boolean mustSee,  TargetingConditions.@Nullable Selector selector) {
        super(mob, LivingEntity.class, 10, mustSee, false, (target, level) -> {
            if (selector != null && !selector.test(target, level)) return false;
            return mob.getDragonVariant().common().targetingSettings().value().canTargetUntamed(target);
        });
        this.mob = mob;
    }

    public DragonNonTameRandomTargetGoal(URDragonEntity mob, boolean mustSee) {
        this(mob, mustSee, null);
    }

    @Override
    public boolean canUse() {
        return !mob.isTame() && mob.canGrief() && super.canUse();
    }
}
