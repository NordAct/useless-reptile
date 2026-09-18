package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

import java.util.EnumSet;

public class RideableDragonLockLookGoal<T extends URRideableDragonEntity> extends Goal {
    protected final T mob;

    public RideableDragonLockLookGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.hasControllingPassenger();
    }
}
