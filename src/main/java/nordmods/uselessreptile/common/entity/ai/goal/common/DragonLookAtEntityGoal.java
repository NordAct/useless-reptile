package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAtEntityGoal extends LookAtPlayerGoal {
    public DragonLookAtEntityGoal(URDragonEntity mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance, 1);
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (((URDragonEntity)mob).isDancing()) return false;
        return mob.getTarget() == null && !((URDragonEntity)mob).shouldFollow && super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (lookAt != null && ((URDragonEntity)mob).getBodyRotationControl().getBodyRotationControl().canBeRotatedOutsideMoveController() && !((URDragonEntity)mob).getBodyRotationControl().getBodyRotationControl().canHeadReachTarget())
            ((URDragonEntity)mob).getBodyRotationControl().setRotationTarget(lookAt);
    }
}
