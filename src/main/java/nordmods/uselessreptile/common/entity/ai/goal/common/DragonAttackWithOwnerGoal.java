package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;


public class DragonAttackWithOwnerGoal<T extends LivingEntity> extends AttackWithOwnerGoal {

    private final URDragonEntity mob;

    public DragonAttackWithOwnerGoal(URDragonEntity tameable) {
        super(tameable);
        this.mob = tameable;
    }

    @Override
    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) * 5;
    }
}
