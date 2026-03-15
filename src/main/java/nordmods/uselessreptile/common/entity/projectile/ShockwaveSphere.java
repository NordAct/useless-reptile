package nordmods.uselessreptile.common.entity.projectile;

import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.init.URMobEffect;
import nordmods.uselessreptile.common.init.URTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class ShockwaveSphere extends Projectile implements ProjectileDamageHelper {
    private float currentRadius = 0;
    private float prevRadius = 0;
    public static final float MAX_RADIUS = 40;
    public static final float RADIUS_CHANGE_SPEED = 0.8f;
    public static final float POWER = 1;
    private final List<Entity> affected = new ArrayList<>();
    private final List<Entity> prevAffected = new ArrayList<>();
    private boolean spawnSoundPlayed = false;
    public float prevAlpha = 1f;

    public ShockwaveSphere(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
        setNoGravity(true);
        setInvulnerable(true);
        setYRot(new Random(getId()).nextInt(360));
    }

    public ShockwaveSphere(Level world) {
        this(UREntities.SHOCKWAVE_SPHERE, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        prevRadius = currentRadius;
        if (currentRadius <= MAX_RADIUS) {
            List<Entity> targets = level().getEntities(this, getBoundingBox().inflate(currentRadius + 3), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onHitEntity(entityHitResult);
            }
            currentRadius += RADIUS_CHANGE_SPEED;
            prevAffected.clear();
            prevAffected.addAll(affected);
            affected.clear();
        } else discard();
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity target = entityHitResult.getEntity();
        float exposure = ServerExplosion.getSeenPercent(getEyePosition(), target);

        if (exposure > 0) {
            target.playSound(URSoundEvent.SHOCKWAVE_HIT, 1, 1 / exposure);
            Vec3 vec3d = target.position().subtract(getEyePosition());
            double lengthMod = currentRadius / vec3d.length();
            target.addDeltaMovement(vec3d.normalize().scale(POWER * lengthMod * exposure));
            if (target instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel world) {
                livingEntity.addEffect(new MobEffectInstance(URMobEffect.SHOCK, (int) (100 * Mth.clamp(lengthMod, 1, 2) * exposure), 0, false, false), getOwner());
                livingEntity.hurtServer(world, damageSources().source(DamageTypes.LIGHTNING_BOLT, getOwner()), (float) (getResultingDamage() * Mth.clamp(lengthMod, 1, 2)));
            }
        }
        if (!(target instanceof Projectile)) affected.add(target);
    }

    private boolean canTarget(Entity target) {
        if (prevAffected.contains(target)) {
            affected.add(target);
            return false;
        }
        if (target.is(URTags.DRAGON_IMMUNE)) return false;
        if (getEyePosition().distanceTo(target.position()) > currentRadius + target.getBbWidth()/2) return false;
        if (target instanceof EntityPart part) target = part.owner;
        Entity owner = getOwner();
        LivingEntity ownerOwner = owner instanceof OwnableEntity tameable ? tameable.getOwner() : null;
        if (target == ownerOwner) return false;
        return !(target instanceof OwnableEntity tameableEntity) || tameableEntity.getOwner() != ownerOwner;
    }

    private void tryPlaySpawnSound() {
        if (!spawnSoundPlayed) {
            playSound(URSoundEvent.SHOCKWAVE, 1, 1);
            spawnSoundPlayed = true;
        }
    }

    @Override
    public double getEyeY() {
        return position().y + getBbHeight()/2f;
    }

    @Override
    public boolean shouldBeSaved() {
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
