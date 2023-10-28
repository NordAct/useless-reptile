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
            particleSphere(ParticleTypes.ELECTRIC_SPARK, (int) (50 * currentRadius/maxRadius), (int) (50 * currentRadius/maxRadius), getEyePos(), currentRadius, getWorld());

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

    public void particleSphere(ParticleEffect particle, int rowsHorizontal, int rowsVertical, Vec3d pos, float radius, World world) {
        if (!world.isClient()) return;
        float dPitch = 180f / rowsVertical;
        float yaw = 0;
        float pitch = -90;
        for (int j = 0; j <= rowsVertical; j++) {
            int particleAmount = (int) (rowsHorizontal - (rowsHorizontal * Math.abs(pitch/90f)) + 1);
            for (int i = 0; i <= particleAmount; i++) {
                float dYaw = 360f / particleAmount;
                Vec3d rot = getRotationVector(pitch, yaw);
                world.addParticle(particle,
                        pos.getX() + rot.x * radius, pos.getY() + rot.y * radius, pos.getZ() + rot.z * radius,
                        0, 0, 0);
                yaw += dYaw;
            }
            pitch += dPitch;
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
