package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

import java.util.EnumSet;

public class RideableDragonLockLookGoal extends Goal {
    private final URRideableDragonEntity dragon;

    public RideableDragonLockLookGoal(URRideableDragonEntity dragon) {
        this.dragon = dragon;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return dragon.hasControllingPassenger();
    }
}
