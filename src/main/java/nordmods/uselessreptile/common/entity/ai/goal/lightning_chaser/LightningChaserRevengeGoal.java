package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import nordmods.uselessreptile.common.entity.LightningChaser;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonRevengeGoal;

public class LightningChaserRevengeGoal extends DragonRevengeGoal {
    private final LightningChaser mob;
    public LightningChaserRevengeGoal(LightningChaser mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !mob.hasSurrendered() && !mob.getShouldBailOut() && super.canUse();
    }
}
