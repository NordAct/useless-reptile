package nordmods.uselessreptile.common.entity.ai.goal.magmamuncher;

import nordmods.uselessreptile.common.entity.Magmamuncher;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MagmamuncherAttackGoal extends Goal {
    private final Magmamuncher entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public MagmamuncherAttackGoal(Magmamuncher entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        target = entity.getTarget();
    }

    @Override
    public boolean canUse() {
        if (!entity.canAttack(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        return target != null && (entity.distanceToSqr(target) < maxSearchDistance / (entity.isTame() ? 1 : 8));
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive()) {
            return false;
        }
        return !entity.getNavigation().isDone() || canUse();
    }

    @Override
    public void stop() {
        target = null;
        entity.setTarget(null);
        entity.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!canContinueToUse() || target.isRemoved()) {
            stop();
            return;
        }
        entity.setSprinting(true);
        //entity.getLookControl().lookAt(target);
        entity.getNavigation().moveTo(target, 1);

        if (entity.getPrimaryAttackCooldown() > 0 || !entity.getAttackBoundingBox().intersects(target.getBoundingBox())) return;

        entity.attackMelee(target);
    }
}
