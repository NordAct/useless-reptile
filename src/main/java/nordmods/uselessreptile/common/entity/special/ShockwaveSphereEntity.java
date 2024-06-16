package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShockwaveSphereEntity extends ProjectileEntity {
    private float currentRadius = 0;
    public static final float MAX_RADIUS = 20;
    public static final float RADIUS_CHANGE_SPEED = 0.4f;
    public static final float POWER = 1;
    private final List<Entity> affected = new ArrayList<>();
    private final List<Entity> prevAffected = new ArrayList<>();
    private boolean spawnSoundPlayed = false;
    public float prevAlpha = 1f;

    public ShockwaveSphereEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        setNoGravity(true);
        setInvulnerable(true);
        setYaw(new Random(getId()).nextInt(360));
    }

    public ShockwaveSphereEntity(World world) {
        this(UREntities.SHOCKWAVE_SPHERE_ENTITY, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (currentRadius <= MAX_RADIUS) {
            setPosition(getPos().subtract(0, RADIUS_CHANGE_SPEED, 0));
            calculateDimensions();
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
            }

            currentRadius += RADIUS_CHANGE_SPEED;
            prevAffected.clear();
            prevAffected.addAll(affected);
            affected.clear();
        } else discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();
        float exposure = Explosion.getExposure(getEyePos(), target);

        if (exposure > 0) {
            target.playSound(URSounds.SHOCKWAVE_HIT, 1, 1 / exposure);
            Vec3d vec3d = target.getPos().subtract(getEyePos());
            double lengthMod = currentRadius / vec3d.length();
            target.addVelocityInternal(vec3d.normalize().multiply(POWER * lengthMod * exposure));
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(URStatusEffects.SHOCK, (int) (100 * MathHelper.clamp(lengthMod, 1, 2) * exposure), 0, false, false), getOwner());
                livingEntity.damage(getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner()), (float) (2.5 * MathHelper.clamp(lengthMod, 1, 2)));
            }
        }
        affected.add(target);
    }

    private boolean canTarget(Entity target) {
        if (prevAffected.contains(target)) return false;
        if (getEyePos().distanceTo(target.getPos()) > currentRadius + target.getWidth()/2) return false;
        if (target.isInvulnerableTo(getDamageSources().create(DamageTypes.LIGHTNING_BOLT))) return false;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof TameableEntity tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        if (target instanceof TameableEntity tameableEntity && tameableEntity.getOwner() == ownerOwner) return false;

        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(currentRadius * 2, currentRadius * 2);
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSounds.SHOCKWAVE, 1, 1);
            spawnSoundPlayed = true;
        }
    }

    @Override
    public double getEyeY() {
        return getPos().y + currentRadius;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    public float getCurrentRadius() {
        return currentRadius;
    }
}
