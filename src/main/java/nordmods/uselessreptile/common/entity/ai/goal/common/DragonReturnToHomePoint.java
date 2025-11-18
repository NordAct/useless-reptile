package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class DragonReturnToHomePoint extends Goal {
    private final URDragonEntity entity;
    private final int toleranceDistance = 20;

    public DragonReturnToHomePoint(URDragonEntity entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return entity.isTame() && entity.distanceToSqr(entity.getHomePoint().getCenter()) > toleranceDistance * toleranceDistance;
    }

    @Override
    public boolean canContinueToUse(){
        return entity.distanceToSqr(entity.getHomePoint().getCenter()) > toleranceDistance * toleranceDistance / 2f;
    }

    @Override
    public void tick() {
        entity.getNavigation().moveTo(entity.getHomePoint().getX(), entity.getHomePoint().getY(), entity.getHomePoint().getZ(), 1);
    }
}
