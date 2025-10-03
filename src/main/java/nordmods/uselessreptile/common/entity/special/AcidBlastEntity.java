package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URDamageTypes;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcidBlastEntity extends URMovingProjectile implements GeoEntity, ProjectileDamageHelper {
    private static final int COLOR = 0x99E416;
    public AcidBlastEntity(EntityType<? extends AcidBlastEntity> entityType, World world) {
        super(entityType, world);
        lifeLimit = 200;
    }

    public AcidBlastEntity(World world, LivingEntity owner) {
        this(UREntities.ACID_BLAST_ENTITY, world);
        setOwner(owner);
        pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!getEntityWorld().isClient()) spawnEffectCloud();
        super.onBlockHit(blockHitResult);
        discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!(getEntityWorld() instanceof ServerWorld world)) return;
        Entity target = entityHitResult.getEntity();
        target.damage(world, target.getDamageSources().create(URDamageTypes.ACID, getOwner()), getResultingDamage());
        spawnEffectCloud();
        super.onEntityHit(entityHitResult);
        if (target instanceof LivingEntity entity) entity.addStatusEffect(new StatusEffectInstance(URStatusEffects.ACID, 60, 1));
        discard();

    }

    private void spawnEffectCloud() {
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(getEntityWorld(), getX(), getY(), getZ());
        Entity entity = getOwner();
        if (entity instanceof LivingEntity livingEntity) areaEffectCloudEntity.setOwner(livingEntity);
        playSound(URSounds.ACID_SPLASH, 1, 1);
        areaEffectCloudEntity.setRadius(1.0f);
        areaEffectCloudEntity.setDuration(20);
        areaEffectCloudEntity.setWaitTime(0);
        areaEffectCloudEntity.setRadiusGrowth(0.1f);
        areaEffectCloudEntity.addEffect(new StatusEffectInstance(URStatusEffects.ACID, 10, 1));
        areaEffectCloudEntity.setSilent(true);
        getEntityWorld().spawnEntity(areaEffectCloudEntity);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (getEntityWorld().isClient()) spawnEffectParticles(8, COLOR);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>("contr", 0, animationEvent -> {
            animationEvent.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    protected SoundEvent getHitSound() {
        return URSounds.ACID_SPLASH;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public float getDefaultDamage() {
        return 3;
    }

    @Override
    public float getDamageScaling() {
        return 1;
    }
}