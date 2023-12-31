package nordmods.uselessreptile.common.entity.ai.goal.moleclaw;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

import java.util.EnumSet;

public class MoleclawAttackGoal extends Goal {
    private final MoleclawEntity entity;
    private LivingEntity target;
    private final double maxSearchDistance;
    private int attackDelay = 0;
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
        if (entity.isTargetFriendly(entity.getTarget())) entity.setTarget(null);
        LivingEntity livingEntity = entity.getTarget();
        boolean tooBright = target != null && entity.isTooBrightAtPos(target.getBlockPos());
        return livingEntity != null && (!(entity.squaredDistanceTo(livingEntity) > maxSearchDistance)) && !tooBright;
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
        float yawChange = entity.getRotationSpeed();
        entity.lookAtEntity(target, yawChange, entity.getPitchLimit());
        entity.getNavigation().startMovingTo(target, 1);
        boolean doesCollide = entity.doesCollide(entity.getAttackBox(), target.getBoundingBox());

        if (!entity.isMoving()) notMovingTimer++;
        else notMovingTimer = 0;
        if (notMovingTimer >= nextStrongAttackTimer && entity.getPrimaryAttackCooldown() == 0) {
            BlockHitResult hitResult = (BlockHitResult) entity.raycast(4, 1, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                attackDelay = 4;
                entity.setPrimaryAttackCooldown(entity.getMaxPrimaryAttackCooldown());
                nextStrongAttackTimer = entity.getRandom().nextInt(21) + 40;
            }
        }
        if (entity.isPrimaryAttack()) {
            if (attackDelay == 0) {
                entity.strongAttack();
                attackDelay = 100;
            }
            else attackDelay--;
        }

        if (entity.isSecondaryAttack()) {
            if (attackDelay == 0) {
                entity.meleeAttack();
                attackDelay = 100;
            }
            else attackDelay--;
        }

        if (entity.getSecondaryAttackCooldown() > 0) return;
        if (!doesCollide) return;

        attackDelay = 8;
        entity.setSecondaryAttackCooldown(entity.getMaxSecondaryAttackCooldown());
    }
}