package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

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
        if (mob instanceof ShooterDragon shooterDragon) {
            Vec3 point = shooterDragon.getShootingPoint().pos();
            mob.getLookControl().setLookAt(point.x + x, point.y, point.z + z);
        }
        else mob.getLookControl().setLookAt(mob.getX() + x, mob.getEyeY(), mob.getZ() + z);
    }
}
