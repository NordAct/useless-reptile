package nordmods.uselessreptile.common.entity;

import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserBailOutGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRevengeGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRoamAroundGoal;
import nordmods.uselessreptile.common.entity.animation_processor.DragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.animation_processor.MultipartDragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.URNetworkHelper;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class LightningChaser extends URRideableFlyingDragonEntity implements MultipartEntity {
    private int bailOutTimer = 6000;
    private boolean shouldBailOut = false;
    private boolean isChallenger = false;
    private static final Identifier THUNDERSTORM_BONUS = UselessReptile.id("thunderstorm_bonus");
    private final URDragonPart head = new URDragonPart(this, "head", 0.875f, 0.875f);
    private final URDragonPart neck1 = new URDragonPart(this, "neck1", 0.875f, 0.875f);
    private final URDragonPart neck2 = new URDragonPart(this, "neck2", 0.875f, 0.875f);
    private final URDragonPart neck3 = new URDragonPart(this, "neck3", 0.875f, 0.875f);
    private final URDragonPart front = new URDragonPart(this, "front", 1.125f, 1.125f);
    private final URDragonPart back = new URDragonPart(this, "back", 1.125f, 1.125f);
    private final URDragonPart tail1 = new URDragonPart(this, "tail1");
    private final URDragonPart tail2 = new URDragonPart(this, "tail2");
    private final URDragonPart tail3 = new URDragonPart(this, "tail3");
    private final URDragonPart tail4 = new URDragonPart(this, "tail4");
    private final URDragonPart wingLeft = new URDragonPart(this, "wing_left", 1.5f, 1f);
    private final URDragonPart wingRight = new URDragonPart(this, "wing_right", 1.5f, 1f);
    private final URDragonPart shoulderArmLeft = new URDragonPart(this, "shoulder_arm_left", 3f, 1.5f);
    private final URDragonPart shoulderArmRight = new URDragonPart(this, "shoulder_arm_right", 3f, 1.5f);
    private final URDragonPart fingersLeft = new URDragonPart(this, "fingers_left", 3f, 2f);
    private final URDragonPart fingersRight = new URDragonPart(this, "fingers_right", 3f, 2f);
    private final URDragonPart[] parts = new URDragonPart[]{head, neck1, neck2, neck3, front, back, tail1, tail2, tail3, tail4, wingLeft, wingRight, shoulderArmLeft, shoulderArmRight, fingersLeft, fingersRight};
    protected final DynamicGameEventListener<LightningStrikeEventListener> lightningStrikeEventHandler = new DynamicGameEventListener<>(new LightningStrikeEventListener
            (new EntityPositionSource(this, getEyeHeight()), URGameEvents.LIGHTNING_STRIKE_FAR.value().notificationRadius()));
    public static final float BASE_GROUND_SPEED = 0.25f;
    private static final EntityDimensions FLYING_FORWARD = EntityDimensions.scalable(2.95f, 1).withEyeHeight(0.9f);
    private static final EntityDimensions ON_GROUND = EntityDimensions.scalable(2.95f, 2.95f).withEyeHeight(2.9f);

    public LightningChaser(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;
        pitchLimitGround = 70;
        pitchLimitAir = 45;
        ticksUntilHeal = 500;
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (data == FLYING || data == MOVING || data == MOVING_BACKWARDS) {
            refreshDimensions();
        }
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal(this));
        goalSelector.addGoal(5, new LightningChaserAttackGoal(this));
        goalSelector.addGoal(6, new LightningChaserRoamAroundGoal(this));
        goalSelector.addGoal(6, new LightningChaserBailOutGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 60));
        goalSelector.addGoal(8, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(10, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(11, new DragonLookAroundGoal(this));
        targetSelector.addGoal(1, new LightningChaserRevengeGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, Player.class, true, null));

    }

    @Override
    public SpawnGroupData finalizeSpawn(@NonNull ServerLevelAccessor world, @NonNull DifficultyInstance difficulty, @NonNull EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason == EntitySpawnReason.EVENT) isChallenger = true;
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    public void tickAnimations() {
        if (level().isClientSide()) return;
        tickBlinkController();
        tickMainController();
    }

    private void tickBlinkController() {
        URDragonAnimationController<URDragonEntity> blinkController = getAnimationController(AnimationController.BLINK);
        if (blinkController.isPlayingAbilityAnimation(AnimationController.BLINK)) return;
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickMainController() {
        URDragonAnimationController<URDragonEntity> mainController = getAnimationController(AnimationController.MAIN);
        float animationSpeed = getMovementSpeedModifier();
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(animationSpeed));
        if (mainController.isPlayingAbilityAnimation(AnimationController.MAIN)) return;
        if (isFlying()) {
            if (isMoving()) {
                if (isMovingBackwards()) {
                    mainController.playAnimation("fly.back");
                    return;
                }
                if (getTiltState() == TiltState.DOWN) {
                    if (getAccelerationModifier() > 0.5f && getXBodyRot(1) > 20) {
                        mainController.playAnimation("fly.down");
                        return;
                    }
                    if (getAccelerationModifier() > 0.15f && getXBodyRot(1) > 5) {
                        mainController.playAnimation("fly.glide");
                        return;
                    }
                }
                if (isFlyGliding()) {
                    mainController.playAnimation("fly.glide");
                    return;
                }
                mainController.playAnimation("fly.straight");
                return;
            }
            mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(Math.max(animationSpeed, 1)));
            mainController.playAnimation("fly.idle");
            return;
        }
        if (isOrderedToSit() && !isDancing()) {
            mainController.playAnimation("sit");
            return;
        }
        if (isMoving() || isMoveForwardPressed()) {
            mainController.playAnimation("walk");
            return;
        }
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(1));
        if (isDancing() && !isVehicle()) {
            mainController.playAnimation("dance");
            return;
        }
        mainController.playAnimation("idle");
    }

    public static AttributeSupplier.Builder createLightningChaserAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().lightningChaserDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().lightningChaserKnockback)
                .add(Attributes.MAX_HEALTH, attributes().lightningChaserHealth)
                .add(Attributes.ARMOR, attributes().lightningChaserArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().lightningChaserArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().lightningChaserGroundSpeed)
                .add(Attributes.FLYING_SPEED, attributes().lightningChaserFlyingSpeed)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().lightningChaserVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().lightningChaserBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().lightningChaserRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().lightningChaserRotationSpeedAir);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SURRENDERED, false);
    }
    public static final EntityDataAccessor<Boolean> SURRENDERED = SynchedEntityData.defineId(LightningChaser.class, EntityDataSerializers.BOOLEAN);
    public boolean hasSurrendered() {return entityData.get(SURRENDERED);}
    public void setSurrendered(boolean state) {entityData.set(SURRENDERED, state);}

    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        if (!isTame()) {
            tag.putInt("BailOutTimer", bailOutTimer);
            tag.putBoolean("HasSurrendered", hasSurrendered());
            tag.putBoolean("BailingOut", shouldBailOut);
            tag.putBoolean("IsChallenger", isChallenger);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        if (!isTame()) {
            bailOutTimer = tag.getIntOr("BailOutTimer", bailOutTimer);
            setSurrendered(tag.getBooleanOr("HasSurrendered", false));
            shouldBailOut = tag.getBooleanOr("BailingOut", false);
            isChallenger = tag.getBooleanOr("IsChallenger", false);
        }
    }

    public void playAmbientSound() {
        boolean playRoar = !isTame() && isFlying() && level().isThundering() && !getShouldBailOut() && !hasSurrendered();
        SoundInfo soundInfo = getSoundInfo(playRoar ? "roar" : "idle");
        if (soundInfo != null) playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
    }

    @Override
    public boolean isInvulnerableTo(@NonNull ServerLevel world, @NonNull DamageSource damageSource) {
        if (damageSource.is(DamageTypes.LIGHTNING_BOLT)) return true;
        else return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    public void tick() {
        tickAnimations();
        super.tick();
        updateThunderstormBonus();

        if (!level().isClientSide() && !shouldBailOut) {
            if (isChallenger) {
                if (getTarget() == null && !isTame()) {
                    if (bailOutTimer > 0) bailOutTimer--;
                    else {
                        setSurrendered(false);
                        shouldBailOut = true;
                    }
                }
            } else if (getHealth() / getMaxHealth() > 0.5) setSurrendered(false);
            if (hasSurrendered()) {
                if (tickCount % 200 == 0) heal(2);
                setOrderedToSit(true);
                ejectPassengers();
            }
        }

        getLookControl().setLockRotation(hasSurrendered() && !isFlying());

        if (isFlying()) {
            fingersRight.setScale(1, 1);
            fingersLeft.setScale(1, 1);
            shoulderArmRight.setScale(1, 1);
            shoulderArmLeft.setScale(1, 1);
        } else {
            fingersRight.setScale(1f, 0.5f);
            fingersLeft.setScale(1f, 0.5f);
            shoulderArmRight.setScale(1.25f, 0.5f);
            shoulderArmLeft.setScale(1.25f, 0.5f);
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions;
        if (isFlying()) {
            if (isMoving() && !isMovingBackwards()) dimensions = FLYING_FORWARD;
            else dimensions = ON_GROUND;
        } else {
            dimensions = ON_GROUND;
        }
        return dimensions.scale(getAgeScale());
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.MEDIUM;
    }

    @Override
    public DragonVariantType<? extends DragonVariant> getVariantType() {
        return URDragonVariantTypes.LIGHTNING_CHASER;
    }

    private void updateThunderstormBonus() {
        if (level().isClientSide()) return;
        if (level().isThundering()) {
            tryAddModifier(Attributes.ARMOR, 4, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.FLYING_SPEED, 0.2, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.ATTACK_DAMAGE, 2f, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(URAttributes.DRAGON_ACCELERATION_DURATION, -0.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_VERTICAL_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            removeModifier(Attributes.ARMOR);
            removeModifier(Attributes.FLYING_SPEED);
            removeModifier(Attributes.MOVEMENT_SPEED);
            removeModifier(Attributes.ATTACK_DAMAGE);
            removeModifier(URAttributes.DRAGON_ACCELERATION_DURATION);
            removeModifier(URAttributes.DRAGON_VERTICAL_SPEED);
            removeModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED);
            removeModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED);
        }
    }

    private void tryAddModifier(Holder<Attribute> entityAttribute, double bonus, AttributeModifier.Operation operation) {
        if (!getAttribute(entityAttribute).hasModifier(THUNDERSTORM_BONUS))
            getAttribute(entityAttribute)
                    .addTransientModifier(new AttributeModifier(THUNDERSTORM_BONUS, bonus, operation));
    }

    private void removeModifier(Holder<Attribute> entityAttribute) {
        getAttribute(entityAttribute).removeModifier(THUNDERSTORM_BONUS);
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource damageSource, float amount) {
        boolean toReturn = super.hurtServer(world, damageSource, amount);
        if (getHealth() / getMaxHealth() < 0.3 && !hasSurrendered() && (getTamingProgress() <= 0 || isTame())) {
            if (!isDeadOrDying()) setHealth(getMaxHealth() * 0.3f);
            setInAirTimer(getMaxInAirTimer());
            setTarget(null);
            setSurrendered(true);
            setInAirTimer(getMaxInAirTimer());
            if (damageSource.getEntity() != null) setHomePoint(damageSource.getEntity().blockPosition());
            else setHomePoint(blockPosition());
            URNetworkHelper.playSound(this, URSoundEvent.LIGHTNING_CHASER_SURRENDER, getSoundSource(), 1, 1,1);
            if (isChallenger) bailOutTimer = 6000;
        }
        return toReturn;
    }

    @Override
    public void thunderHit(@NonNull ServerLevel world, @NonNull LightningBolt lightning) {
        if (isTameable() && lightning.getCause() != null && getTamingProgress() > 0) setTamingProgress(getTamingProgress() - 1);
        addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 3));
        addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1));
        addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 1));
        addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
        lightning.discard();
    }

    public void triggerShoot() { //todo remove
        getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().getType().equals(URDragonAbilityTypes.LIGHTNING_BREATH_ATTACK))
                .findFirst()
                .ifPresent(DragonAbilityHolder::use);
    }

    @Override
    public int getMaxHeadYRot() {
        return 70;
    }

    public void triggerShockwave() { //todo remove
        getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().getType().equals(URDragonAbilityTypes.SHOCKWAVE_ATTACK))
                .findFirst()
                .ifPresent(DragonAbilityHolder::use);
    }

    public void meleeAttack() { //todo remove
        getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().getType().equals(URDragonAbilityTypes.MELEE_ATTACK))
                .findFirst()
                .ifPresent(DragonAbilityHolder::use);
    }

    @Override
    public @NonNull AABB getPrimaryAttackBox() {
        Vec3 rotationVec = calculateViewVector(0, getYRot()).scale(2.5);
        return getBoundingBox().move(rotationVec);
    }

    @Override
    protected int getTicksUntilHeal() {
        return level().isThundering() ? (int) (super.getTicksUntilHeal() * 0.5) : super.getTicksUntilHeal();
    }

    @Override
    public String getDefaultVariant() {
        return "grey";
    }

    @Override
    public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (isTameable()) {
            if (hasSurrendered() && !getShouldBailOut() && getTamingProgress() <= 0 || player.isCreative() && getFoodItem(itemStack) != null) {
                tame(player);
                setPersistenceRequired();
                setSurrendered(false);
                shouldBailOut = false;
                isChallenger = false;
                level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                return InteractionResult.SUCCESS;
            } else if (hasSurrendered() && getTamingProgress() > 0) {
                level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean startRiding(@NonNull Entity entity, boolean force, boolean event) {
        if (hasSurrendered()) return false;
        return super.startRiding(entity, force, event);
    }

    public boolean getShouldBailOut() {
        return shouldBailOut;
    }

    public boolean isChallenger() {
        return isChallenger;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().lightningChaserMaxGroupSize * 2;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (hasSurrendered() || getShouldBailOut()) return false;
        return super.canAttack(target);
    }

    protected class LightningStrikeEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public LightningStrikeEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NonNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(@NonNull ServerLevel world, @NonNull Holder<GameEvent> event, GameEvent.@NonNull Context emitter, @NonNull Vec3 emitterPos) {
            if (event != URGameEvents.LIGHTNING_STRIKE_FAR) return false;
            if (isTame() || getTarget() != null) return false;
            if (emitter.sourceEntity() instanceof LightningBolt lightning) {
                Player target = lightning.getCause();
                if (target != null) {
                    if (!canAttack(target)) return false;
                    setTarget(target);
                    URNetworkHelper.playSound(LightningChaser.this, URSoundEvent.LIGHTNING_CHASER_ACCEPT_CHALLENGE, getSoundSource(), 1, 1,1);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void updateDynamicGameEventListener(@NonNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
        if (level() instanceof ServerLevel serverWorld) callback.accept(lightningStrikeEventHandler, serverWorld);
        super.updateDynamicGameEventListener(callback);
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(level() instanceof ServerLevel world)) return false;
        boolean shouldBreakBlocks = isTame() ? URConfig.getConfig().lightningChaserGriefing.canTamedBreak() : URConfig.getConfig().lightningChaserGriefing.canUntamedBreak();
        return shouldBreakBlocks && world.getGameRules().get(GameRules.MOB_GRIEFING);
    }

    @Override
    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        return isChallenger || shouldBailOut || super.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    @Override
    public boolean shouldFlyDown() {
        return super.shouldFlyDown() || (hasSurrendered() && !shouldBailOut && isFlying());
    }

    @Override
    public boolean shouldBeSaved() {
        return super.shouldBeSaved() && !shouldBailOut;
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    @Override
    public @Nullable DragonAnimationProcessor<? extends URDragonEntity> createServerAnimationProcessor() {
        return new MultipartDragonAnimationProcessor<>(this);
    }
}
