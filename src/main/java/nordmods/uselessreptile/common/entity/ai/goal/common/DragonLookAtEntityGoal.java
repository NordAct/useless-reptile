package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAtEntityGoal<T extends URDragonEntity> extends LookAtPlayerGoal {
    protected final T mob;
    public DragonLookAtEntityGoal(T mob, Class<? extends LivingEntity> lookAtType, float lookDistance) {
        super(mob, lookAtType, lookDistance);
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.isDancing()) return false;
        return mob.getTarget() == null && !mob.shouldFollow && super.canUse();
    }

    @Override
    public void tick() {
        super.tick();
        if (lookAt != null && mob.getBodyRotationControl().getBodyRotationControl().canBeRotatedOutsideMoveController() && !mob.getBodyRotationControl().getBodyRotationControl().canHeadReachTarget())
            mob.getBodyRotationControl().setRotationTarget(lookAt);
    }
}
