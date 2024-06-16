package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.shape.VoxelShape;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

import java.util.EnumSet;

public class MoleclawAttackGoal extends Goal {
    private final MoleclawEntity entity;
    private LivingEntity target;
    private final double maxSearchDistance;
    private int notMovingTimer = 0;
    private int nextStrongAttackTimer = 60;

    public MoleclawAttackGoal(MoleclawEntity entity, double maxSearchDistance) {
        this.entity = entity;
        this.maxSearchDistance = maxSearchDistance;
        setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public void start() {
        entity.setPrimaryAttackCooldown(Math.max(entity.getPrimaryAttackCooldown(), 20));
        target = entity.getTarget();
    }

    @Override
    public boolean canStart() {
        if (entity.canBeControlledByRider()) return false;
        if (entity.isTargetFriendly(entity.getTarget())) {
            entity.setTarget(null);
            return false;
        }
        target = entity.getTarget();
        if (target == null) return false;
        boolean tooBright = entity.isTooBrightAtPos(target.getBlockPos());
        return !tooBright && (entity.squaredDistanceTo(target) < maxSearchDistance);
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
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        entity.setSprinting(true);
        entity.lookAt(target);
        entity.getNavigation().startMovingTo(target, 1);

        if (!entity.isMoving()) notMovingTimer++;
        else notMovingTimer = 0;
        if (notMovingTimer >= nextStrongAttackTimer && entity.getPrimaryAttackCooldown() == 0) {
            int any = 0;
            for (VoxelShape ignored : entity.getWorld().getBlockCollisions(null, entity.getSecondaryAttackBox())) any++;
            if (any > 0) {
                entity.scheduleStrongAttack();
                nextStrongAttackTimer = entity.getRandom().nextInt(21) + 40;
            }
        }

        if (entity.getSecondaryAttackCooldown() > 0) return;
        boolean doesCollide = entity.doesCollide(entity.getAttackBox(), target.getBoundingBox());
        if (doesCollide) entity.scheduleNormalAttack();
    }
}