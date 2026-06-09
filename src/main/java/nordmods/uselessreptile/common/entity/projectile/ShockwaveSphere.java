package nordmods.uselessreptile.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URMobEffect;
import nordmods.uselessreptile.common.init.URSoundEvent;
import nordmods.uselessreptile.common.init.URTags;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ShockwaveSphere extends Projectile implements ProjectileDamageHelper, BRAnimatedObject {
    private float currentRadius = 0;
    private float prevRadius = 0;
    public static final float MAX_RADIUS_BLOCKS = 40;
    public static final float RADIUS_CHANGE_SPEED_PER_TICK = 0.8f;
    public static final float POWER = 1;
    private final List<Entity> affected = new ArrayList<>();
    private final List<Entity> prevAffected = new ArrayList<>();
    private boolean spawnSoundPlayed = false;
    public float prevAlpha = 1f;
    public float damageScaling = 0.5f;
    private float defaultDamage = 4;
    public float power = 1;

    public ShockwaveSphere(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
        setNoGravity(true);
        setInvulnerable(true);
        setYRot(new Random().nextInt(360));
    }

    public ShockwaveSphere(Level world) {
        this(UREntities.SHOCKWAVE_SPHERE, world);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("DamageScaling", damageScaling);
        output.putFloat("DefaultDamage", defaultDamage);
        output.putFloat("Power", power);
        output.putFloat("RadiusChangeSpeed", getRadiusChangeSpeed());
        output.putFloat("MaxRadius", getMaxRadius());
        output.putInt("Color", getColor());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        damageScaling = input.getFloatOr("DamageScaling", damageScaling);
        defaultDamage = input.getFloatOr("DefaultDamage", defaultDamage);
        power = input.getFloatOr("Power", power);
        setRadiusChangeSpeed(input.getFloatOr("RadiusChangeSpeed", 0.8f));
        setMaxRadius(input.getFloatOr("MaxRadius", 40));
        setColor(input.getIntOr("Color", 0xFFFFFF));
    }

    public static final EntityDataAccessor<Float> MAX_RADIUS = SynchedEntityData.defineId(ShockwaveSphere.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> RADIUS_CHANGE_SPEED = SynchedEntityData.defineId(ShockwaveSphere.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ShockwaveSphere.class, EntityDataSerializers.INT);

    public float getMaxRadius() {return entityData.get(MAX_RADIUS);}
    public void setMaxRadius(float state) {entityData.set(MAX_RADIUS, state);}

    public float getRadiusChangeSpeed() {return entityData.get(RADIUS_CHANGE_SPEED);}
    public void setRadiusChangeSpeed(float state) {entityData.set(RADIUS_CHANGE_SPEED, state);}

    public int getColor() {return entityData.get(COLOR);}
    public void setColor(int state) {entityData.set(COLOR, state);}


    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(MAX_RADIUS, MAX_RADIUS_BLOCKS);
        builder.define(RADIUS_CHANGE_SPEED, RADIUS_CHANGE_SPEED_PER_TICK);
        builder.define(COLOR, 0xFFFFFF);
    }

    @Override
    public void tick() {
        super.tick();
        tryPlaySpawnSound();
        prevRadius = currentRadius;
        if (currentRadius <= getMaxRadius()) {
            List<Entity> targets = level().getEntities(this, getBoundingBox().inflate(currentRadius + 3), this::canTarget);
            for (Entity target : targets) {
                EntityHitResult entityHitResult = new EntityHitResult(target);
                onHitEntity(entityHitResult);
            }
            currentRadius += getRadiusChangeSpeed();
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
            target.addDeltaMovement(vec3d.normalize().scale(power * lengthMod * exposure));
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
        return defaultDamage;
    }

    @Override
    public float getDamageScaling() {
        return damageScaling;
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of();
    }
}
