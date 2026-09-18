package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.LightningChaser;
import nordmods.uselessreptile.common.init.URDragonAbilityTypes;

import java.util.EnumSet;
import java.util.List;

public class LightningChaserAttackGoal extends Goal {

    private final LightningChaser mob;
    private LivingEntity target;
    private int attackCooldown = 20;
    private static final int MIN_DISTANCE_SQUARED = 80;
    private static final int MAX_DISTANCE_SQUARED = (int) (50 * 50 * 0.81f); //todo

    public LightningChaserAttackGoal(LightningChaser mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mob.hasSurrendered() || mob.getShouldBailOut()) return false;

        if (mob.hasControllingPassenger()) return false;
        if (!mob.canAttack(mob.getTarget())) return false;
        target = mob.getTarget();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        return canUse();
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

        double distance = mob.distanceToSqr(target);
        double yDiff = target.getY() - mob.getY();
        if (yDiff > mob.getBbHeight() && !mob.isFlying()) mob.startToFly();
        boolean canSee = mob.canBreakBlocks() || mob.getSensing().hasLineOfSight(target);
        boolean canDamage = !target.isInvulnerableTo((ServerLevel) target.level(), mob.damageSources().source(DamageTypes.LIGHTNING_BOLT, mob)) && canSee;
        double desiredY = target.getY() + (canDamage ? 2 : 0) + target.getBbHeight();
        if (mob.onGround() && !mob.getSensing().hasLineOfSight(target) && canDamage) mob.forceFlightNextTick();
        mob.getLookControl().setLookAt(target);
        if (mob.getNavigation().isDone() && canSee) {
            mob.getBodyRotationControl().setRotationTarget(target);
        }

        if (distance < MIN_DISTANCE_SQUARED && canDamage) { //too close
            mob.getNavigation().stop();
            mob.getMoveControl().moveBack();
            if (target instanceof Player player && yDiff < player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
                    || target instanceof Mob mob && mob.isWithinMeleeAttackRange(this.mob)
                    || mob.getLastAttacker() instanceof Mob attacker && attacker.isWithinMeleeAttackRange(mob)) {
                if (!mob.isFlying()) { //try jump/back off
                    mob.forceFlightNextTick();
                    Vec3 vec3d = mob.calculateViewVector(Mth.clamp(-mob.getXRot(), -10, 10), mob.getYRot() - 180);
                    mob.push(vec3d.scale(2));
                } else {
                    mob.getMoveControl().forceFlyUp();
                    mob.getMoveControl().moveBack();
                }
            }
        } else if (distance < MAX_DISTANCE_SQUARED && canDamage) { //within range
            if (!mob.getLookControl().canLookAtTarget()) {
                double distanceXZ = Math.pow(target.getX() - mob.getX(), 2) * Math.pow(target.getZ() - mob.getZ(), 2);
                double divergence = Math.max(0, (distanceXZ - MAX_DISTANCE_SQUARED / 8f) * 0.25);
                if (distanceXZ < MAX_DISTANCE_SQUARED / 8f) mob.getMoveControl().moveBack();
                else if (desiredY + divergence < mob.getY() || desiredY - divergence > mob.getY())  mob.getMoveControl().moveBack();
                else {
                    if (yDiff > mob.getBbHeight()) mob.getMoveControl().forceFlyUp();
                    else if (yDiff < -mob.getBbHeight()) mob.getMoveControl().forceFlyDown();
                }
            } else {//try compensate momentum
                if (mob.getDeltaMovement().y > 0) mob.getMoveControl().forceFlyDown();
                else if (mob.getDeltaMovement().y < 0) mob.getMoveControl().forceFlyUp();
            }
        } else mob.getNavigation().moveTo(target.getX(), desiredY, target.getZ(), 1); //out of reach/can't be damaged by range attack

        if (--attackCooldown <= 0) {
            if (tryMeleeAttack()) return;
            if (canDamage) {
                if (tryRangedAttack()) return;
                if (tryShockwaveAttack()) {
                }
            }
        }
    }

    private boolean tryMeleeAttack() {
        if (mob.getAvailableAbilities().stream().noneMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0)) return false;
        if (mob.isFlying()) return false;
        boolean doesCollide = mob.getPrimaryAttackBox().intersects(target.getBoundingBox());
        if (!doesCollide) return false;
        mob.meleeAttack();
        attackCooldown = 30;
        return true;
    }

    private boolean tryRangedAttack() {
        if (mob.getAvailableAbilities().stream().noneMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.LIGHTNING_BREATH_ATTACK) && a.getCooldown() <= 0)) return false;
        if (!mob.getLookControl().isLookingAtTarget()) return false;
        double distance = mob.distanceToSqr(target);
        if (distance > MAX_DISTANCE_SQUARED || distance < MIN_DISTANCE_SQUARED) return false;
        mob.triggerShoot();
        attackCooldown = 40;
        return true;
    }

    private boolean tryShockwaveAttack() { //todo redo attack goals
        if (mob.getAvailableAbilities().stream().noneMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.SHOCKWAVE_ATTACK) && a.getCooldown() <= 0)) return false;
        if (!mob.isFlying()) return false;
        //double attackDistance = ShockwaveSphere.MAX_RADIUS * ShockwaveSphere.MAX_RADIUS * 0.49;
        double attackDistance = 40 * 40 * 0.49;
        List<Entity> projectiles = mob.level().getEntities(mob, new AABB(mob.blockPosition()).inflate(attackDistance * 2), c -> c instanceof Projectile projectile && projectile.getOwner() == target && !projectile.getDeltaMovement().equals(Vec3.ZERO));
        if (!projectiles.isEmpty()) {
            mob.triggerShockwave();
            return true;
        }
        double distance = mob.distanceToSqr(target);
        if (attackDistance < distance) return false;
        if (ServerExplosion.getSeenPercent(mob.position(), target) < 0.1) return false;
        mob.triggerShockwave();
        attackCooldown = 40;
        return true;
    }
}
