package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawUntamedTargetGoal<T extends LivingEntity>  extends UntamedActiveTargetGoal<T> {

    private final MoleclawEntity mob;
    public MoleclawUntamedTargetGoal(TameableEntity tameable, Class targetClass) {
        super(tameable, targetClass, true, null);
        mob = (MoleclawEntity) tameable;
    }

    public boolean canStart() {
        boolean sup = super.canStart();
        if (targetEntity != null) return sup && !mob.isTooBrightAtPos(targetEntity.getBlockPos());
        else return sup;
    }

    public boolean shouldContinue() {
        return targetEntity != null ? !mob.isTooBrightAtPos(targetEntity.getBlockPos()) && targetPredicate.test(mob, targetEntity) : super.shouldContinue();
    }
}
