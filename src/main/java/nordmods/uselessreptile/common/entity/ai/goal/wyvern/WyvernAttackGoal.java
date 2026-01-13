package nordmods.uselessreptile.common.entity.ai.goal.wyvern;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.Wyvern;

import java.util.EnumSet;

public class WyvernAttackGoal extends Goal {
    private final Wyvern entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public WyvernAttackGoal(Wyvern entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        entity.setPrimaryAttackCooldown(Math.max(entity.getPrimaryAttackCooldown(), 20));
        target = entity.getTarget();
    }

    @Override
    public boolean canUse() {
        if (entity.hasControllingPassenger()) return false;
        if (!entity.canAttack(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        return target != null && (entity.distanceToSqr(target) < maxSearchDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
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
        if (target.isRemoved()) {
            stop();
            return;
        }
        entity.setSprinting(true);
        double attackDistance = entity.getBbWidth() * 2.0f * (entity.getBbWidth() * 2.0f);
        double distance = entity.distanceToSqr(target);
        entity.getNavigation().moveTo(target, 1);
        boolean doesCollide = entity.getPrimaryAttackBox().intersects(target.getBoundingBox());

        if (!doesCollide && entity.getPrimaryAttackCooldown() == 0 && (distance > attackDistance * 4 || !target.onGround() || distance < attackDistance && entity.getY() - target.getY() >= 1)) {
            entity.getLookControl().setLookAt(target);
            if (entity.getLookControl().isLookingAtTarget())
                entity.shoot();
        }

        if (entity.getSecondaryAttackCooldown() > 0) return;
        if (doesCollide) entity.meleeAttack();
    }
}