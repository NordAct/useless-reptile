package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonRevengeGoal<T extends URDragonEntity> extends HurtByTargetGoal {
    protected final T mob;
    public DragonRevengeGoal(T mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (super.canUse()) return mob.canAttack(mob.getLastHurtByMob());
        else return false;
    }
}
