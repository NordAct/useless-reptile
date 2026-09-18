package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.shapes.VoxelShape;
import nordmods.uselessreptile.common.entity.Moleclaw;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.EnumSet;

public class MoleclawAttackGoal extends Goal {
    private final Moleclaw mob;
    private LivingEntity target;
    private final double maxSearchDistance;
    private int notMovingTimer = 0;
    private int nextStrongAttackTimer = 60;

    public MoleclawAttackGoal(Moleclaw mob, double maxSearchDistance) {
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
        if (target == null) return false;
        boolean tooBright = mob.isTooBrightAtPos(target.blockPosition());
        return !tooBright && (mob.distanceToSqr(target) < maxSearchDistance);
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
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        mob.setSprinting(true);
        mob.getNavigation().moveTo(target, 1);

        if (!mob.isMoving()) notMovingTimer++;
        else notMovingTimer = 0;
        if (notMovingTimer >= nextStrongAttackTimer && mob.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.BLOCK_BREAKING_MELEE_ATTACK_ABILITY) && a.getCooldown() <= 0)) {
            int any = 0;
            for (VoxelShape ignored : mob.level().getBlockCollisions(null, mob.getPrimaryAttackBox())) any++;
            if (any > 0) {
                mob.scheduleStrongAttack();
                nextStrongAttackTimer = mob.getRandom().nextInt(21) + 40;
            }
        }

        if (mob.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0)) return;
        boolean doesCollide = mob.getSecondaryAttackBox().intersects(target.getBoundingBox());
        if (doesCollide) mob.scheduleNormalAttack();
    }
}