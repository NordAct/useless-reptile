package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonRevengeGoal;

public class LightningChaserRevengeGoal extends DragonRevengeGoal {
    private final LightningChaserEntity mob;
    public LightningChaserRevengeGoal(LightningChaserEntity mob, Class<?>... noRevengeTypes) {
        super(mob, noRevengeTypes);
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        return !mob.hasSurrendered() && !mob.getShouldBailOut() && super.canStart();
    }
}
