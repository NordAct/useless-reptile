package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAroundGoal extends Goal {
    protected final URDragonEntity mob;
    protected int lookTimer;
    protected float x;
    protected float z;

    public DragonLookAroundGoal(URDragonEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
        return mob.getRandom().nextFloat() < 0.02;
    }

    @Override
    public boolean canContinueToUse() {
        return lookTimer >= 0;
    }

    @Override
    public void tick() {
        --lookTimer;
        mob.getLookControl().setLookAt(mob.getX() + x, mob.getEyeY(), mob.getZ() + z);
    }
}
