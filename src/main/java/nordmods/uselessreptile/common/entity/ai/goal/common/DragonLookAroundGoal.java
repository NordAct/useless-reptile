package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.LookAroundGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class DragonLookAroundGoal extends LookAroundGoal {
    private final URDragonEntity mob;

    public DragonLookAroundGoal(URDragonEntity mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (this.mob.isDancing()) return false;
        return super.canStart();
    }
}
