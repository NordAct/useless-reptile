package nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import nordmods.uselessreptile.common.entity.RiverPikehorn;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.EnumSet;

public class PikehornAttackGoal extends Goal {
    private final RiverPikehorn entity;
    private LivingEntity target;
    private final double maxSearchDistance;

    public PikehornAttackGoal(RiverPikehorn entity, double maxSearchDistance) {
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
        entity.getNavigation().moveTo(target, 1);

        if (entity.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0) || !entity.getPrimaryAttackBox().intersects(target.getBoundingBox())) return;

        entity.attackMelee(target);
    }
}