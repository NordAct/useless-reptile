package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.network.URPacketHelper;

import java.util.ArrayList;
import java.util.List;

public class ShockwaveSphereEntity extends ProjectileEntity {
    private float currentRadius = 0;
    public final float maxRadius;
    public final float radiusChangeSpeed;
    public final float power;
    private final List<Entity> affected = new ArrayList<>();
    private final List<Entity> prevAffected = new ArrayList<>();
    public final List<Vec3d> sphereDots = new ArrayList<>();
    public static final int SPHERE_ROWS = 16;
    private boolean spawnSoundPlayed = false;

    public ShockwaveSphereEntity(EntityType<? extends ProjectileEntity> entityType, World world, float maxRadius, float radiusChangeSpeed, float power) {
        super(entityType, world);
        this.maxRadius = maxRadius;
        this.radiusChangeSpeed = radiusChangeSpeed;
        this.power = power;
        setNoGravity(true);
        setInvulnerable(true);
    }

    public ShockwaveSphereEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, 20, 0.4f, 1);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        if (currentRadius <= maxRadius) {
            setPosition(getPos().subtract(0, radiusChangeSpeed, 0));
            calculateDimensions();
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
            }
            getSphereDots(currentRadius);

            currentRadius += radiusChangeSpeed;
            prevAffected.clear();
            prevAffected.addAll(affected);
            affected.clear();
        } else discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();

        target.playSound(URSounds.SHOCKWAVE_HIT, 1, 1);
        Vec3d vec3d = target.getPos().subtract(getEyePos());
        double lengthMod = currentRadius / vec3d.length();
        target.addVelocity(vec3d.normalize().multiply(power * lengthMod));
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(URStatusEffects.SHOCK, (int) (100 * MathHelper.clamp(lengthMod, 1, 2)), 0, false, false), getOwner());
            livingEntity.damage(getDamageSources().create(DamageTypes.LIGHTNING_BOLT, getOwner()), (float) (2.5 * MathHelper.clamp(lengthMod, 1, 2)));
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
        return super.getDimensions(pose).scaled(currentRadius*2, currentRadius*2);
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSounds.SHOCKWAVE, 1, 1);
            spawnSoundPlayed = true;
        }
    }

    private void getSphereDots(float radius) {
        if (!getWorld().isClient()) return;
        sphereDots.clear();
        float dPitch = 180f / SPHERE_ROWS;
        float dYaw = 360f / SPHERE_ROWS;
        float yaw = 0;
        float pitch = -90;
        for (int j = 0; j <= SPHERE_ROWS; j++) {
            for (int i = 0; i <= SPHERE_ROWS; i++) {
                Vec3d rot = getRotationVector(pitch, yaw);
                sphereDots.add(new Vec3d(rot.x * radius, rot.y * radius + currentRadius, rot.z * radius));
                yaw += dYaw;
            }
            pitch += dPitch;
        }
    }

    private void particleSphere(ParticleEffect particle) {
        if (!getWorld().isClient()) return;
        for (Vec3d dot : sphereDots) getWorld().addParticle(particle, dot.getX() + getPos().x , dot.getY()  + getPos().y, dot.getZ() + getPos().z, 0, 0, 0);
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
