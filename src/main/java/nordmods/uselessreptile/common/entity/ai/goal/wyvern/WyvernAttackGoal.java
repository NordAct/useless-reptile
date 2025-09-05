package nordmods.uselessreptile.common.entity.ai.goal.wyvern;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.WyvernEntity;

import java.util.EnumSet;

public class WyvernAttackGoal extends Goal {
    private final WyvernEntity entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public WyvernAttackGoal(WyvernEntity entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public void start() {
        entity.setPrimaryAttackCooldown(Math.max(entity.getPrimaryAttackCooldown(), 20));
        target = entity.getTarget();
    }

    @Override
    public boolean canStart() {
        if (entity.canBeControlledByRider()) return false;
        if (!entity.canTarget(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        return target != null && (entity.squaredDistanceTo(target) < maxSearchDistance);
    }

    @Override
    public boolean shouldContinue() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
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
        if (target.isRemoved()) {
            stop();
            return;
        }
        entity.setSprinting(true);
        double attackDistance = entity.getWidth() * 2.0f * (entity.getWidth() * 2.0f);
        double distance = entity.squaredDistanceTo(target);
        entity.getNavigation().startMovingTo(target, 1);
        boolean doesCollide = entity.getAttackBox().intersects(target.getBoundingBox());

        if (!doesCollide && entity.getPrimaryAttackCooldown() == 0 && (distance > attackDistance * 4 || !target.isOnGround() || distance < attackDistance && entity.getY() - target.getY() >= 1)) {
            if (entity.getLookControl().isLookingAtTarget()) entity.shoot();
        }

        if (entity.getSecondaryAttackCooldown() > 0) return;
        if (doesCollide) entity.meleeAttack();
    }
}