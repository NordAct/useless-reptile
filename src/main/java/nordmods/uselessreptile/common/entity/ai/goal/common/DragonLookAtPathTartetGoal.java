package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAtPathTartetGoal<T extends URDragonEntity> extends Goal {
    protected final T mob;

    public DragonLookAtPathTartetGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !mob.getNavigation().isDone() && !mob.shouldFollow && mob.getTarget() == null;
    }

    @Override
    public void tick() {
        if (mob.getNavigation().getTargetPos() != null) {
            Vec3 pos = Vec3.atCenterOf(mob.getNavigation().getTargetPos());
            mob.getLookControl().setLookAt(pos.x, mob.getEyeY(), pos.z);
        }
    }
}
