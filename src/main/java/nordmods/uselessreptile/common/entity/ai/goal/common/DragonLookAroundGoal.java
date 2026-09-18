package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAroundGoal<T extends URDragonEntity> extends Goal {
    protected final T mob;
    protected int lookTimer;
    protected float x;
    protected float z;

    public DragonLookAroundGoal(T mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public void start() {
        super.start();
        float angle = mob.getRandom().nextFloat() * Mth.PI * 2;
        x = Mth.cos(angle);
        z = Mth.sin(angle);
        lookTimer = 20 + mob.getRandom().nextInt(20);
    }

    @Override
    public boolean canUse() {
        if (mob.isDancing()) return false;
        return mob.getTarget() == null && !mob.shouldFollow && mob.getRandom().nextFloat() < 0.02;
    }

    @Override
    public boolean canContinueToUse() {
        return lookTimer >= 0;
    }

    @Override
    public void tick() {
        --lookTimer;
        Vec3 target = new Vec3(mob.getX() + x, mob.getEyeY(), mob.getZ() + z);
        mob.getLookControl().setLookAt(target);
        if (mob.getBodyRotationControl().getBodyRotationControl().canBeRotatedOutsideMoveController() && !mob.getBodyRotationControl().getBodyRotationControl().canHeadReachTarget())
            mob.getBodyRotationControl().setRotationTarget(target);
    }
}
