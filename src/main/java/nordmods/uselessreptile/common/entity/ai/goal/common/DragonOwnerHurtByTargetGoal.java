package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonOwnerHurtByTargetGoal extends OwnerHurtByTargetGoal {
    private final URDragonEntity mob;
    public DragonOwnerHurtByTargetGoal(URDragonEntity tameAnimal) {
        super(tameAnimal);
        mob = tameAnimal;
    }

    @Override
    public boolean canUse() {
        return mob.getDragonVariant().common().targetingSettings().value().defendsOwner() && super.canUse();
    }
}
