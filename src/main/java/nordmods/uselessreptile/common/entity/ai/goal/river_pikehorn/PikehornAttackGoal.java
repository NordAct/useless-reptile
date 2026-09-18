package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.EnumSet;

public class PikehornAttackGoal extends Goal {
    private final RiverPikehorn mob;
    private LivingEntity target;
    private final double maxSearchDistance;

    public PikehornAttackGoal(RiverPikehorn mob, double maxSearchDistance) {
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
        if (!mob.canAttack(mob.getTarget())) {
            mob.setTarget(null);
            return false;
        }
        target = mob.getTarget();
        return target != null && (mob.distanceToSqr(target) < maxSearchDistance / (mob.isTame() ? 1 : 8));
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive()) {
            return false;
        }
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
        if (!canContinueToUse() || target.isRemoved()) {
            stop();
            return;
        }
        mob.setSprinting(true);
        mob.getNavigation().moveTo(target, 1);

        if (mob.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0) || !mob.getPrimaryAttackBox().intersects(target.getBoundingBox())) return;

        mob.attackMelee(target);
    }
}