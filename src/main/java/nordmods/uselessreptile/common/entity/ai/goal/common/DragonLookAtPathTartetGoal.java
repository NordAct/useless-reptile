package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAtPathTartetGoal extends Goal {
    private final URDragonEntity dragon;

    public DragonLookAtPathTartetGoal(URDragonEntity dragon) {
        this.dragon = dragon;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !dragon.getNavigation().isDone() && !dragon.shouldFollow && dragon.getTarget() == null;
    }

    @Override
    public void tick() {
        if (dragon.getNavigation().getTargetPos() != null) {
            Vec3 pos = Vec3.atCenterOf(dragon.getNavigation().getTargetPos());
            dragon.getLookControl().setLookAt(pos.x, dragon.getEyeY(), pos.z);
        }
    }
}
