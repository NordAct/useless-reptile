package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonReturnToHomePoint extends Goal {
    private final URDragonEntity entity;

    public DragonReturnToHomePoint(URDragonEntity entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return entity.isTame() && entity.distanceToSqr(Vec3.atCenterOf(entity.getHomePoint())) > entity.getWanderRadius().radius * entity.getWanderRadius().radius;
    }

    @Override
    public boolean canContinueToUse(){
        return entity.distanceToSqr(Vec3.atCenterOf(entity.getHomePoint())) > entity.getWanderRadius().radius * entity.getWanderRadius().radius / 2f;
    }

    @Override
    public void tick() {
        entity.getNavigation().moveTo(entity.getHomePoint().getX(), entity.getHomePoint().getY(), entity.getHomePoint().getZ(), 1);
    }
}
