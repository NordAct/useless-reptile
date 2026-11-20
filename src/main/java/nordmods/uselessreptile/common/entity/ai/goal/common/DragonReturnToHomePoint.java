package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonReturnToHomePoint<T extends URDragonEntity> extends Goal {
    protected final T mob;

    public DragonReturnToHomePoint(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return mob.isTame() && mob.distanceToSqr(Vec3.atCenterOf(mob.getHomePoint())) > mob.getWanderRadius().radius * mob.getWanderRadius().radius;
    }

    @Override
    public boolean canContinueToUse(){
        return mob.distanceToSqr(Vec3.atCenterOf(mob.getHomePoint())) > mob.getWanderRadius().radius * mob.getWanderRadius().radius / 2f;
    }

    @Override
    public void tick() {
        mob.getNavigation().moveTo(mob.getHomePoint().getX(), mob.getHomePoint().getY(), mob.getHomePoint().getZ(), 1);
    }
}
