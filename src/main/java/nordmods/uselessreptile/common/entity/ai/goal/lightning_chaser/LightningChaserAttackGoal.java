package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.ExplosionImpl;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;

import java.util.EnumSet;
import java.util.List;

public class LightningChaserAttackGoal extends Goal {

    private final LightningChaserEntity entity;
    private LivingEntity target;
    private int attackCooldown = 20;
    private static final int MIN_DISTANCE_SQUARED = 80;
    private static final int MAX_DISTANCE_SQUARED = (int) (LightningBreathEntity.MAX_LENGTH * LightningBreathEntity.MAX_LENGTH * 0.81f);

    public LightningChaserAttackGoal(LightningChaserEntity entity) {
        this.entity = entity;
        setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (entity.hasSurrendered() || entity.getShouldBailOut()) return false;

        if (entity.canBeControlledByRider()) return false;
        if (!entity.canTarget(entity.getTarget())) return false;
        target = entity.getTarget();
        return target != null;
    }

    @Override
    public boolean shouldContinue() {
        if (target == null) return false;
        if (!target.isAlive()) return false;
        return canStart();
    }

    @Override
    public void stop() {
        target = null;
        entity.setTarget(null);
        entity.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null || target.isRemoved()) {
            stop();
            return;
        }
        entity.setSprinting(true);

        double distance = entity.squaredDistanceTo(target);
        double yDiff = target.getY() - entity.getY();
        if (yDiff > entity.getHeight() && !entity.isFlying()) entity.startToFly();
        boolean canSee = entity.canBreakBlocks() || entity.getVisibilityCache().canSee(target);
        boolean canDamage = !target.isInvulnerableTo((ServerWorld) target.getWorld(), entity.getDamageSources().create(DamageTypes.LIGHTNING_BOLT, entity)) && canSee;
        double desiredY = target.getY() + (canDamage ? 2 : 0) + target.getHeight();
        if (entity.isOnGround() && !entity.getVisibilityCache().canSee(target) && canDamage) entity.forceFlightNextTick();

        if (entity.getNavigation().isIdle())
            entity.getLookControl().lookAt(target);
        if (distance < MIN_DISTANCE_SQUARED && canDamage) { //too close
            entity.getNavigation().stop();
            entity.getMoveControl().moveBack();
            if (target instanceof PlayerEntity player && yDiff < player.getAttributeValue(EntityAttributes.ENTITY_INTERACTION_RANGE)
                    || target instanceof MobEntity mob && mob.getAttackBox().intersects(entity.getBoundingBox().expand(2))
                    || entity.getLastAttacker() instanceof MobEntity attacker && attacker.getAttackBox().intersects(entity.getBoundingBox().expand(2))) {
                if (!entity.isFlying()) { //try jump/back off
                    entity.forceFlightNextTick();
                    Vec3d vec3d = entity.getRotationVector(MathHelper.clamp(-entity.getPitch(), -10, 10), entity.getYaw() - 180);
                    entity.addVelocity(vec3d.multiply(2));
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
                    if (yDiff > entity.getHeight()) entity.getMoveControl().forceFlyUp();
                    else if (yDiff < -entity.getHeight()) entity.getMoveControl().forceFlyDown();
                }
            } else {//try compensate momentum
                if (entity.getVelocity().y > 0) entity.getMoveControl().forceFlyDown();
                else if (entity.getVelocity().y < 0) entity.getMoveControl().forceFlyUp();
            }
        } else entity.getNavigation().startMovingTo(target.getX(), desiredY, target.getZ(), 1); //out of reach/can't be damaged by range attack

        if (--attackCooldown <= 0) {
            if (tryMeleeAttack()) return;
            if (canDamage) {
                if (tryRangedAttack()) return;
                if (tryShockwaveAttack()) return;
            }
        }
    }

    private boolean tryMeleeAttack() {
        if (entity.getSecondaryAttackCooldown() > 0) return false;
        if (entity.isFlying()) return false;
        boolean doesCollide = entity.getAttackBox().intersects(target.getBoundingBox());
        if (!doesCollide) return false;
        entity.meleeAttack();
        attackCooldown = 30;
        return true;
    }

    private boolean tryRangedAttack() {
        if (entity.getPrimaryAttackCooldown() > 0) return false;
        if (!entity.getLookControl().isLookingAtTarget()) return false;
        double distance = entity.squaredDistanceTo(target);
        if (distance > MAX_DISTANCE_SQUARED || distance < MIN_DISTANCE_SQUARED) return false;
        entity.triggerShoot();
        attackCooldown = 40;
        return true;
    }

    private boolean tryShockwaveAttack() {
        if (entity.getSpecialAttackCooldown() > 0) return false;
        if (!entity.isFlying()) return false;
        double attackDistance = ShockwaveSphereEntity.MAX_RADIUS * ShockwaveSphereEntity.MAX_RADIUS * 0.49;
        List<Entity> projectiles = entity.getWorld().getOtherEntities(entity, new Box(entity.getBlockPos()).expand(attackDistance * 2), c -> c instanceof ProjectileEntity projectile && projectile.getOwner() == target && !projectile.getVelocity().equals(Vec3d.ZERO));
        if (!projectiles.isEmpty()) {
            entity.triggerShockwave();
            return true;
        }
        double distance = entity.squaredDistanceTo(target);
        if (attackDistance < distance) return false;
        if (ExplosionImpl.calculateReceivedDamage(entity.getPos(), target) < 0.1) return false;
        entity.triggerShockwave();
        attackCooldown = 40;
        return true;
    }
}
