package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URStatusEffects;

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

    public ShockwaveSphereEntity(EntityType<? extends ProjectileEntity> entityType, World world, float maxRadius, float radiusChangeSpeed, float power) {
        super(entityType, world);
        this.maxRadius = maxRadius;
        this.radiusChangeSpeed = radiusChangeSpeed;
        this.power = power;
        setNoGravity(true);
    }

    public ShockwaveSphereEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, 20, 0.4f, 1);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();
        if (currentRadius <= maxRadius) {
            setPosition(getPos().subtract(0, radiusChangeSpeed, 0));
            calculateDimensions();
            List<Entity> targets = getWorld().getOtherEntities(this, getBoundingBox(),
                    entity -> !entity.isInvulnerableTo(getDamageSources().create(DamageTypes.LIGHTNING_BOLT)));
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onEntityHit(entityHitResult);
            }
            getSphereDots(SPHERE_ROWS, getEyePos(), currentRadius);

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
        if (prevAffected.contains(target)) return;
        if (getEyePos().distanceTo(target.getPos()) > currentRadius + target.getWidth()/2) return;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof TameableEntity tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return;
        if (target instanceof TameableEntity tameableEntity && tameableEntity.getOwner() == ownerOwner) return;

        Vec3d vec3d = target.getPos().subtract(getEyePos());
        target.addVelocity(vec3d.normalize().multiply(power * currentRadius / vec3d.length()));
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(URStatusEffects.SHOCK, 100, 0, false, false), owner);
            livingEntity.damage(getDamageSources().lightningBolt(), 5);
        }
        
        affected.add(target);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(currentRadius*2, currentRadius*2);
    }

    private void getSphereDots(int rows, Vec3d pos, float radius) {
        if (!getWorld().isClient()) return;
        sphereDots.clear();
        float dPitch = 180f / rows;
        float dYaw = 360f / rows;
        float yaw = 0;
        float pitch = -90;
        for (int j = 0; j <= rows; j++) {
            for (int i = 0; i <= rows; i++) {
                Vec3d rot = getRotationVector(pitch, yaw);
                sphereDots.add(new Vec3d(pos.getX() + rot.x * radius, pos.getY() + rot.y * radius, pos.getZ() + rot.z * radius));
                yaw += dYaw;
            }
            pitch += dPitch;
        }
    }

    private void particleSphere(ParticleEffect particle) {
        if (!getWorld().isClient()) return;
        for (Vec3d dot : sphereDots) getWorld().addParticle(particle, dot.getX() , dot.getY(), dot.getZ(), 0, 0, 0);
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
