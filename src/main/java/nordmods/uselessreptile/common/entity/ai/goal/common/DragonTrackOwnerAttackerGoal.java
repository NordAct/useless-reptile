package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonTrackOwnerAttackerGoal extends TrackOwnerAttackerGoal {
    private final URDragonEntity mob;
    public DragonTrackOwnerAttackerGoal(URDragonEntity mob) {
        super(mob);
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
