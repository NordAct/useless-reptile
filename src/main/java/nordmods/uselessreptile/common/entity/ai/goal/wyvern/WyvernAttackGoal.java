package nordmods.uselessreptile.common.entity.ai.goal.wyvern;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.Wyvern;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.EnumSet;

public class WyvernAttackGoal extends Goal {
    private final Wyvern mob;
    private LivingEntity target;
    private final double maxSearchDistance;

    public WyvernAttackGoal(Wyvern mob, double maxSearchDistance) {
        this.mob = mob;
        this.maxSearchDistance = maxSearchDistance;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public void start() {
        target = mob.getTarget();
    }

    @Override
    public boolean canUse() {
        if (mob.hasControllingPassenger()) return false;
        if (!mob.canAttack(mob.getTarget())) {
            mob.setTarget(null);
            return false;
        }
        target = mob.getTarget();
        return target != null && (mob.distanceToSqr(target) < maxSearchDistance);
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        return !mob.getNavigation().isDone() || canUse();
    }

    @Override
    public void stop() {
        target = null;
        mob.setTarget(null);
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null || target.isRemoved()) {
            stop();
            return;
        }
        mob.setSprinting(true);
        double attackDistance = mob.getBbWidth() * 2.0f * (mob.getBbWidth() * 2.0f);
        double distance = mob.distanceToSqr(target);
        mob.getNavigation().moveTo(target, 1);
        boolean doesCollide = mob.getPrimaryAttackBox().intersects(target.getBoundingBox());

        if (!doesCollide && mob.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.SHOT_ATTACK) && a.getCooldown() <= 0) && (distance > attackDistance * 4 || !target.onGround() || distance < attackDistance && mob.getY() - target.getY() >= 1)) {
            mob.getLookControl().setLookAt(target);
            if (mob.getLookControl().isLookingAtTarget())
                mob.shoot();
        }

        if (doesCollide && mob.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0))
            mob.meleeAttack();
    }
}