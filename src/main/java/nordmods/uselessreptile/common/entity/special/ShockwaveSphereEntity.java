package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionImpl;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShockwaveSphereEntity extends ProjectileEntity implements ProjectileDamageHelper {
    private float currentRadius = 0;
    private float prevRadius = 0;
    public static final float MAX_RADIUS = 40;
    public static final float RADIUS_CHANGE_SPEED = 0.8f;
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
        prevRadius = currentRadius;
        if (currentRadius <= MAX_RADIUS) {
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox().expand(currentRadius + 3), this::canTarget);
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
        float exposure = ExplosionImpl.calculateReceivedDamage(getEyePos(), target);

        if (exposure > 0) {
            target.playSound(URSounds.SHOCKWAVE_HIT, 1, 1 / exposure);
            Vec3d vec3d = target.getPos().subtract(getEyePos());
            double lengthMod = currentRadius / vec3d.length();
            target.addVelocityInternal(vec3d.normalize().multiply(POWER * lengthMod * exposure));
            if (target instanceof LivingEntity livingEntity && livingEntity.getWorld() instanceof ServerWorld world) {
                livingEntity.addStatusEffect(new StatusEffectInstance(URStatusEffects.SHOCK, (int) (100 * MathHelper.clamp(lengthMod, 1, 2) * exposure), 0, false, false), getOwner());
                livingEntity.damage(world, getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner()), (float) (getResultingDamage() * MathHelper.clamp(lengthMod, 1, 2)));
            }
        }
        if (!(target instanceof ProjectileEntity)) affected.add(target);
    }

    private boolean canTarget(Entity target) {
        if (prevAffected.contains(target)) {
            affected.add(target);
            return false;
        }
        if (getEyePos().distanceTo(target.getPos()) > currentRadius + target.getWidth()/2) return false;
        if (target instanceof EntityPart part) target = part.owner;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof Tameable tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        if (target instanceof Tameable tameableEntity && tameableEntity.getOwner() == ownerOwner) return false;

        return true;
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSounds.SHOCKWAVE, 1, 1);
            spawnSoundPlayed = true;
        }
    }

    @Override
    public double getEyeY() {
        return getPos().y + getHeight()/2f;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    public float getCurrentRadius() {
        return currentRadius;
    }

    public float getPrevRadius() {
        return prevRadius;
    }

    @Override
    public float getDefaultDamage() {
        return 4;
    }

    @Override
    public float getDamageScaling() {
        return 0.5f;
    }
}
