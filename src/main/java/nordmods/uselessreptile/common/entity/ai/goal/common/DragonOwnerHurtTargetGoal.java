package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonOwnerHurtTargetGoal extends OwnerHurtTargetGoal {
    private final URDragonEntity mob;
    public DragonOwnerHurtTargetGoal(URDragonEntity tameAnimal) {
        super(tameAnimal);
        mob = tameAnimal;
    }

    @Override
    public boolean canUse() {
        return mob.getDragonVariant().common().attacksOwnerTarget() && super.canUse();
    }
}
