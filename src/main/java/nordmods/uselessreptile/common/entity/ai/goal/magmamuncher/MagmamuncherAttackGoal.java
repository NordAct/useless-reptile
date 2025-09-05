package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;

import java.util.EnumSet;

public class MagmamuncherAttackGoal extends Goal {
    private final MagmamuncherEntity entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public MagmamuncherAttackGoal(MagmamuncherEntity entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public void start() {
        target = entity.getTarget();
    }

    @Override
    public boolean canStart() {
        if (!entity.canTarget(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        return target != null && (entity.squaredDistanceTo(target) < maxSearchDistance / (entity.isTamed() ? 1 : 8));
    }

    @Override
    public boolean shouldContinue() {
        if (target == null) return false;
        if (!target.isAlive()) {
            return false;
        }
        return !entity.getNavigation().isIdle() || canStart();
    }

    @Override
    public void stop() {
        target = null;
        entity.setTarget(null);
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!shouldContinue() || target.isRemoved()) {
            stop();
            return;
        }
        entity.setSprinting(true);
        //entity.getLookControl().lookAt(target);
        entity.getNavigation().startMovingTo(target, 1);

        if (entity.getPrimaryAttackCooldown() > 0 || !entity.getAttackBox().intersects(target.getBoundingBox())) return;

        entity.attackMelee(target);
    }
}
