package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.Moleclaw;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonNonTameRandomTargetGoal;

public class MoleclawUntamedTargetGoal<T extends LivingEntity>  extends DragonNonTameRandomTargetGoal<T> {
    private final Moleclaw mob;
    public MoleclawUntamedTargetGoal(Moleclaw tameable, Class targetClass) {
        super(tameable, targetClass, true, null);
        mob = tameable;
    }

    public boolean canUse() {
        boolean sup = super.canUse();
        if (target != null) return sup && !mob.isTooBrightAtPos(target.blockPosition());
        else return sup;
    }

    public boolean canContinueToUse() {
        return target != null ? !mob.isTooBrightAtPos(target.blockPosition()) && targetConditions.test(getServerLevel(mob), mob, target) : super.canContinueToUse();
    }
}
