package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.shapes.VoxelShape;
import nordmods.uselessreptile.common.entity.Moleclaw;

import java.util.EnumSet;

public class MoleclawAttackGoal extends Goal {
    private final Moleclaw entity;
    private LivingEntity target;
    private final double maxSearchDistance;
    private int notMovingTimer = 0;
    private int nextStrongAttackTimer = 60;

    public MoleclawAttackGoal(Moleclaw entity, double maxSearchDistance) {
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
        if (entity.canBeControlledByRider()) return false;
        if (!entity.canAttack(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        if (target == null) return false;
        boolean tooBright = entity.isTooBrightAtPos(target.blockPosition());
        return !tooBright && (entity.distanceToSqr(target) < maxSearchDistance);
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
        entity.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        entity.setSprinting(true);
        entity.getNavigation().moveTo(target, 1);

        if (!entity.isMoving()) notMovingTimer++;
        else notMovingTimer = 0;
        if (notMovingTimer >= nextStrongAttackTimer && entity.getPrimaryAttackCooldown() == 0) {
            int any = 0;
            for (VoxelShape ignored : entity.level().getBlockCollisions(null, entity.getPrimaryAttackBox())) any++;
            if (any > 0) {
                entity.scheduleStrongAttack();
                nextStrongAttackTimer = entity.getRandom().nextInt(21) + 40;
            }
        }

        if (entity.getSecondaryAttackCooldown() > 0) return;
        boolean doesCollide = entity.getSecondaryAttackBox().intersects(target.getBoundingBox());
        if (doesCollide) entity.scheduleNormalAttack();
    }
}