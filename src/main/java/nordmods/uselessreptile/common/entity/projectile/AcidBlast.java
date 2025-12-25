package nordmods.uselessreptile.common.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.uselessreptile.common.init.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class AcidBlast extends URMovingProjectile implements BRAnimatedObject, ProjectileDamageHelper {
    private static final int COLOR = 0x99E416;
    public AcidBlast(EntityType<? extends AcidBlast> entityType, Level world) {
        super(entityType, world);
        lifeLimit = 200;
    }

    public AcidBlast(Level world, LivingEntity owner) {
        this(UREntities.ACID_BLAST_ENTITY, world);
        setOwner(owner);
        pickup = Pickup.DISALLOWED;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if (!level().isClientSide()) spawnEffectCloud();
        super.onHitBlock(blockHitResult);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (!(level() instanceof ServerLevel world)) return;
        Entity target = entityHitResult.getEntity();
        if (!target.getType().is(URTags.DRAGON_IMMUNE)) target.hurtServer(world, target.damageSources().source(URDamageTypes.ACID, getOwner()), getResultingDamage());
        spawnEffectCloud();
        super.onHitEntity(entityHitResult);
        if (target instanceof LivingEntity entity) entity.addEffect(new MobEffectInstance(URMobEffect.ACID, 60, 1));
        discard();

    }

    private void spawnEffectCloud() {
        AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(level(), getX(), getY(), getZ());
        Entity entity = getOwner();
        if (entity instanceof LivingEntity livingEntity) areaEffectCloudEntity.setOwner(livingEntity);
        playSound(URSoundEvent.ACID_SPLASH, 1, 1);
        areaEffectCloudEntity.setRadius(1.0f);
        areaEffectCloudEntity.setDuration(20);
        areaEffectCloudEntity.setWaitTime(0);
        areaEffectCloudEntity.setRadiusPerTick(0.1f);
        areaEffectCloudEntity.addEffect(new MobEffectInstance(URMobEffect.ACID, 10, 1));
        areaEffectCloudEntity.setSilent(true);
        level().addFreshEntity(areaEffectCloudEntity);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) spawnEffectParticles(8, COLOR);
    }

//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
//        animatableManager.add(new AnimationController<>("contr", 0, animationEvent -> {
//            animationEvent.controller().setAnimation(RawAnimation.begin().thenLoop("idle"));
//            return PlayState.CONTINUE;
//        }));
//    }

    @Override
    protected @NotNull SoundEvent getDefaultHitGroundSoundEvent() {
        return URSoundEvent.ACID_SPLASH;
    }

    @Override
    public float getDefaultDamage() {
        return 3;
    }

    @Override
    public float getDamageScaling() {
        return 1;
    }

    @Override
    public Collection<BRAnimationController<?>> getAnimationControllers() {
        return List.of();//todo
    }
}