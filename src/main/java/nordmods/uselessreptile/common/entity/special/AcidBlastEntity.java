package nordmods.uselessreptile.common.entity.special;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.init.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcidBlastEntity extends PersistentProjectileEntity implements GeoEntity {

    private int life;
    private final int color = 10085398;

    public AcidBlastEntity(EntityType<? extends AcidBlastEntity> entityType, World world) {
        super(entityType, world, ItemStack.EMPTY);
    }

    public AcidBlastEntity(World world, LivingEntity owner) {
        super(UREntities.ACID_BLAST_ENTITY, world, ItemStack.EMPTY);
        setOwner(owner);
    }

    protected void age() {
        ++life;
        if (life >= 100) {
            discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!getWorld().isClient()) spawnEffectCloud();
        super.onBlockHit(blockHitResult);
        discard();
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof EntityPart entityPart && entityPart.owner == getOwner()) return false;
        return super.canHit(entity);
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!isSilent()) getWorld().playSound(getX(), getY(),getZ(), sound, SoundCategory.NEUTRAL, volume, pitch,true);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (getWorld().isClient()) return;
        Entity target = entityHitResult.getEntity();
        target.damage(target.getDamageSources().create(URDamageTypes.ACID, getOwner()),2 * URMobAttributesConfig.getConfig().dragonDamageMultiplier);
        spawnEffectCloud();
        playSound(URSounds.ACID_SPLASH, 1, 1);
        super.onEntityHit(entityHitResult);
        if (target instanceof LivingEntity entity) entity.addStatusEffect(new StatusEffectInstance(URStatusEffects.ACID, 60, 1));
        discard();

    }

    private void spawnEffectCloud() {
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(getWorld(), getX(), getY(), getZ());
        Entity entity = getOwner();
        if (entity instanceof LivingEntity livingEntity) areaEffectCloudEntity.setOwner(livingEntity);

        areaEffectCloudEntity.setColor(color);
        areaEffectCloudEntity.setParticleType(ParticleTypes.ENTITY_EFFECT);
        areaEffectCloudEntity.setRadius(1.0f);
        areaEffectCloudEntity.setDuration(20);
        areaEffectCloudEntity.setRadiusGrowth(0.1f);
        areaEffectCloudEntity.addEffect(new StatusEffectInstance(URStatusEffects.ACID, 10, 1));
        areaEffectCloudEntity.setSilent(true);
        getWorld().spawnEntity(areaEffectCloudEntity);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient()) spawnParticles(8);
        age();
    }

    private void spawnParticles(int amount) {
        int i = color;
        double d = (double)(i >> 16 & 0xFF) / 255.0;
        double e = (double)(i >> 8 & 0xFF) / 255.0;
        double f = (double)(i >> 0 & 0xFF) / 255.0;
        for (int j = 0; j < amount; ++j) {
            getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, getParticleX(0.5), getRandomBodyY(), getParticleZ(0.5), d, e, f);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "contr", 0, animationEvent -> {
            animationEvent.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
}