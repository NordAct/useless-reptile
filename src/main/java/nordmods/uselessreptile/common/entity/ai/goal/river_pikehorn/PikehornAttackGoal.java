package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;

import java.util.EnumSet;

public class PikehornAttackGoal extends Goal {
    private final RiverPikehornEntity entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public PikehornAttackGoal(RiverPikehornEntity entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setControls(EnumSet.of(Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public void start() {
        target = entity.getTarget();
    }

    @Override
    public boolean canStart() {
        if (entity.isTargetFriendly(entity.getTarget())) entity.setTarget(null);
        LivingEntity entityTarget = entity.getTarget();
        return entityTarget != null && (!(entity.squaredDistanceTo(entityTarget) > maxSearchDistance / (entity.isTamed() ? 1 : 8)));
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
        entity.lookAtEntity(target, entity.getRotationSpeed(), 180);
        double attackDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);
        double distance = entity.squaredDistanceTo(target);
        entity.getNavigation().startMovingTo(target, 1);

        if (entity.getPrimaryAttackCooldown() > 0 || distance >= attackDistance) return;

        entity.attackMelee(target);
    }
}