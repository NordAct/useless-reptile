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

    private final LightningChaser entity;
    private LivingEntity target;
    private int attackCooldown = 20;
    private static final int MIN_DISTANCE_SQUARED = 80;
    private static final int MAX_DISTANCE_SQUARED = (int) (50 * 50 * 0.81f); //todo

    public LightningChaserAttackGoal(LightningChaser entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.hasSurrendered() || entity.getShouldBailOut()) return false;

        if (entity.hasControllingPassenger()) return false;
        if (!entity.canAttack(entity.getTarget())) return false;
        target = entity.getTarget();
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
        entity.setTarget(null);
        entity.getNavigation().stop();
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
        entity.setSprinting(true);

        double distance = entity.distanceToSqr(target);
        double yDiff = target.getY() - entity.getY();
        if (yDiff > entity.getBbHeight() && !entity.isFlying()) entity.startToFly();
        boolean canSee = entity.canBreakBlocks() || entity.getSensing().hasLineOfSight(target);
        boolean canDamage = !target.isInvulnerableTo((ServerLevel) target.level(), entity.damageSources().source(DamageTypes.LIGHTNING_BOLT, entity)) && canSee;
        double desiredY = target.getY() + (canDamage ? 2 : 0) + target.getBbHeight();
        if (entity.onGround() && !entity.getSensing().hasLineOfSight(target) && canDamage) entity.forceFlightNextTick();

        if (entity.getNavigation().isDone())
            entity.getLookControl().setLookAt(target);
        if (distance < MIN_DISTANCE_SQUARED && canDamage) { //too close
            entity.getNavigation().stop();
            entity.getMoveControl().moveBack();
            if (target instanceof Player player && yDiff < player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)
                    || target instanceof Mob mob && mob.isWithinMeleeAttackRange(entity)
                    || entity.getLastAttacker() instanceof Mob attacker && attacker.isWithinMeleeAttackRange(entity)) {
                if (!entity.isFlying()) { //try jump/back off
                    entity.forceFlightNextTick();
                    Vec3 vec3d = entity.calculateViewVector(Mth.clamp(-entity.getXRot(), -10, 10), entity.getYRot() - 180);
                    entity.push(vec3d.scale(2));
                } else {
                    entity.getMoveControl().forceFlyUp();
                    entity.getMoveControl().moveBack();
                }
            }
        } else if (distance < MAX_DISTANCE_SQUARED && canDamage) { //within range
            if (!entity.getLookControl().canLookAtTarget()) {
                double distanceXZ = Math.pow(target.getX() - entity.getX(), 2) * Math.pow(target.getZ() - entity.getZ(), 2);
                double divergence = Math.max(0, (distanceXZ - MAX_DISTANCE_SQUARED / 8f) * 0.25);
                if (distanceXZ < MAX_DISTANCE_SQUARED / 8f) entity.getMoveControl().moveBack();
                else if (desiredY + divergence < entity.getY() || desiredY - divergence > entity.getY())  entity.getMoveControl().moveBack();
                else {
                    if (yDiff > entity.getBbHeight()) entity.getMoveControl().forceFlyUp();
                    else if (yDiff < -entity.getBbHeight()) entity.getMoveControl().forceFlyDown();
                }
            } else {//try compensate momentum
                if (entity.getDeltaMovement().y > 0) entity.getMoveControl().forceFlyDown();
                else if (entity.getDeltaMovement().y < 0) entity.getMoveControl().forceFlyUp();
            }
        } else entity.getNavigation().moveTo(target.getX(), desiredY, target.getZ(), 1); //out of reach/can't be damaged by range attack

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
        if (entity.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK) && a.getCooldown() <= 0)) return false;
        if (entity.isFlying()) return false;
        boolean doesCollide = entity.getPrimaryAttackBox().intersects(target.getBoundingBox());
        if (!doesCollide) return false;
        entity.meleeAttack();
        attackCooldown = 30;
        return true;
    }

    private boolean tryRangedAttack() {
        if (entity.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.LIGHTNING_BREATH_ATTACK) && a.getCooldown() <= 0)) return false;
        if (!entity.getLookControl().isLookingAtTarget()) return false;
        double distance = entity.distanceToSqr(target);
        if (distance > MAX_DISTANCE_SQUARED || distance < MIN_DISTANCE_SQUARED) return false;
        entity.triggerShoot();
        attackCooldown = 40;
        return true;
    }

    private boolean tryShockwaveAttack() { //todo redo attack goals
        if (entity.getAvailableAbilities().stream().anyMatch(a -> a.getAbility().getType().equals(URDragonAbilityTypes.SHOCKWAVE_ATTACK) && a.getCooldown() <= 0)) return false;
        if (!entity.isFlying()) return false;
        //double attackDistance = ShockwaveSphere.MAX_RADIUS * ShockwaveSphere.MAX_RADIUS * 0.49;
        double attackDistance = 40 * 40 * 0.49;
        List<Entity> projectiles = entity.level().getEntities(entity, new AABB(entity.blockPosition()).inflate(attackDistance * 2), c -> c instanceof Projectile projectile && projectile.getOwner() == target && !projectile.getDeltaMovement().equals(Vec3.ZERO));
        if (!projectiles.isEmpty()) {
            entity.triggerShockwave();
            return true;
        }
        double distance = entity.distanceToSqr(target);
        if (attackDistance < distance) return false;
        if (ServerExplosion.getSeenPercent(entity.position(), target) < 0.1) return false;
        entity.triggerShockwave();
        attackCooldown = 40;
        return true;
    }
}
