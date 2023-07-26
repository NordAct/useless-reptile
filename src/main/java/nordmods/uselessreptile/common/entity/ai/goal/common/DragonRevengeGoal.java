package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonRevengeGoal extends RevengeGoal {

    private final URDragonEntity mob;
    public DragonRevengeGoal(URDragonEntity mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (super.canStart()) return !mob.isTargetFriendly(mob.getAttacker());
        else return false;
    }

    @Override
    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 5;
    }
}
