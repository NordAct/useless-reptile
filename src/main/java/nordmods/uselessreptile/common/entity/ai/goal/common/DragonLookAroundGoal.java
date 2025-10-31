package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.EnumSet;

public class DragonLookAroundGoal extends Goal {
    protected final URDragonEntity mob;
    protected int lookTimer;
    protected float x;
    protected float z;

    public DragonLookAroundGoal(URDragonEntity mob) {
        this.mob = mob;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public void start() {
        super.start();
        float angle = mob.getRandom().nextFloat() * MathHelper.PI * 2;
        x = MathHelper.cos(angle);
        z = MathHelper.sin(angle);
        lookTimer = 20 + mob.getRandom().nextInt(20);
    }

    @Override
    public boolean canStart() {
        if (mob.isDancing()) return false;
        return mob.getRandom().nextFloat() < 0.02;
    }

    @Override
    public boolean shouldContinue() {
        return lookTimer >= 0;
    }

    @Override
    public void tick() {
        --lookTimer;
        if (mob instanceof ShooterDragon shooterDragon) {
            Vec3d point = shooterDragon.getShootingPoint().pos();
            mob.getLookControl().lookAt(point.x + x, point.y, point.z + z);
        }
        else mob.getLookControl().lookAt(mob.getX() + x, mob.getEyeY(), mob.getZ() + z);
    }
}
