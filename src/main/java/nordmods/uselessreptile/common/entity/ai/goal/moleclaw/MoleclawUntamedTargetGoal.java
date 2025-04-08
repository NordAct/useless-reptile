package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawUntamedTargetGoal<T extends LivingEntity>  extends UntamedActiveTargetGoal<T> {

    private final MoleclawEntity mob;
    public MoleclawUntamedTargetGoal(MoleclawEntity tameable, Class targetClass) {
        super(tameable, targetClass, true, null);
        mob = tameable;
    }

    public boolean canStart() {
        boolean sup = super.canStart();
        if (targetEntity != null) return sup && !mob.isTooBrightAtPos(targetEntity.getBlockPos());
        else return sup;
    }

    public boolean shouldContinue() {
        return targetEntity != null ? !mob.isTooBrightAtPos(targetEntity.getBlockPos()) && targetPredicate.test(getServerWorld(mob), mob, targetEntity) : super.shouldContinue();
    }
}
