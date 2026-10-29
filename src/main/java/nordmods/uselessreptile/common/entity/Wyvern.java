package nordmods.uselessreptile.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.*;
import nordmods.uselessreptile.common.entity.ai.goal.wyvern.WyvernGoAfterFoodGoal;
import nordmods.uselessreptile.common.entity.animation_processor.MultipartDragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;

public class Wyvern extends URRideableFlyingDragonEntity<Wyvern> implements MultipartEntity {
    private final URDragonPart head = new URDragonPart(this, "head", 0.5f, 0.5f);
    private final URDragonPart neck1 = new URDragonPart(this, "neck1", 0.5f, 0.5f);
    private final URDragonPart neck2 = new URDragonPart(this, "neck2", 0.5f, 0.5f);
    private final URDragonPart neck3 = new URDragonPart(this, "neck3", 0.5f, 0.5f);
    private final URDragonPart neck4 = new URDragonPart(this, "neck4", 0.5f, 0.5f);
    private final URDragonPart neck5 = new URDragonPart(this, "neck5", 0.5f, 0.5f);
    private final URDragonPart front = new URDragonPart(this, "front");
    private final URDragonPart back = new URDragonPart(this, "back");
    private final URDragonPart tail1 = new URDragonPart(this, "tail1", 0.75f, 0.75f);
    private final URDragonPart tail2 = new URDragonPart(this, "tail2", 0.75f, 0.75f);
    private final URDragonPart tail3 = new URDragonPart(this, "tail3", 0.75f, 0.75f);
    private final URDragonPart tail4 = new URDragonPart(this, "tail4", 0.75f, 0.75f);
    private final URDragonPart tail5 = new URDragonPart(this, "tail5", 0.75f, 0.75f);
    private final URDragonPart wingLeft = new URDragonPart(this, "wing_left", 1.25f, 1f);
    private final URDragonPart wingRight = new URDragonPart(this, "wing_right", 1.25f, 1f);
    private final URDragonPart shoulderArmLeft = new URDragonPart(this, "shoulder_arm_left", 2f, 1f);
    private final URDragonPart shoulderArmRight = new URDragonPart(this, "shoulder_arm_right", 2f, 1f);
    private final URDragonPart[] parts = new URDragonPart[]{head, neck1, neck2, neck3, neck4, neck5, front, back, tail1, tail2, tail3, tail4, tail5, wingLeft, wingRight, shoulderArmLeft, shoulderArmRight};
    private static final EntityDimensions FLYING_IDLE = EntityDimensions.scalable(1.8f, 2.95f).withEyeHeight(2.9f);
    private static final EntityDimensions FLYING_FORWARD = EntityDimensions.scalable(1.8f, 1).withEyeHeight(0.9f);
    private static final EntityDimensions ON_GROUND = EntityDimensions.scalable(1.8f, 2.95f).withEyeHeight(2.9f);
    private final MultipartDragonAnimationProcessor<Wyvern> processor = new MultipartDragonAnimationProcessor<>(this);

    public Wyvern(EntityType<? extends URRideableFlyingDragonEntity> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;

        ticksUntilHeal = 200;
        sprintSpeedModifier = 1.2f;
        maxXBodyRotSamples = 5;
        setCanPickUpLoot(true);
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
        goalSelector.addGoal(1, new RideableDragonLockLookGoal<>(this));
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal<>(this));
        goalSelector.addGoal(5, new DragonAttackGoal(this));
        goalSelector.addGoal(6, new WyvernGoAfterFoodGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonReturnToHomePoint<>(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal<>(this));
        goalSelector.addGoal(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(10, new FlyingDragonLookAtEntityGoal<>(this, LivingEntity.class, 16));
        goalSelector.addGoal(10, new FlyingDragonLookAroundGoal<>(this));
        goalSelector.addGoal(11, new DragonLookAtPathTartetGoal<>(this));
        targetSelector.addGoal(1, new DragonRevengeGoal<>(this));
        targetSelector.addGoal(2, new DragonOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DragonOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new DragonNonTameRandomTargetGoal(this, true));
        targetSelector.addGoal(4, new DragonTameRandomTargetGoal(this, true));
    }

    public void tickAnimations() {
        if (level().isClientSide()) return;
        tickBlinkController();
        tickMainController();
    }

    private void tickBlinkController() {
        URDragonAnimationController<nordmods.uselessreptile.common.entity.base.URDragonEntity> blinkController = getAnimationController(AnimationController.BLINK);
        if (blinkController.isPlayingAbilityAnimation(AnimationController.BLINK)) return;
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickMainController() {
        URDragonAnimationController<nordmods.uselessreptile.common.entity.base.URDragonEntity> mainController = getAnimationController(AnimationController.MAIN);
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

    @Override
    public void tick() {
        super.tick();
        tickAnimations();
        if (isFlying()) {
            shoulderArmRight.setScale(1, 1);
            shoulderArmLeft.setScale(1, 1);
        } else {
            shoulderArmRight.setScale(0.75f, 0.75f);
            shoulderArmLeft.setScale(0.75f, 0.75f);
        }
        if (isFlying() && isMoving() && !isMovingBackwards()) {
            setYawTargetLimit((int) Math.min(120 + Math.abs(getXRot()/ 1.5f), 180));
            setPitchTargetLimit(90);
        } else {
            setYawTargetLimit(90);
            setPitchTargetLimit(70);
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        EntityDimensions dimensions;
        if (isFlying()) {
            if (isMoving() && !isMovingBackwards()) dimensions = FLYING_FORWARD;
            else dimensions = FLYING_IDLE;
        } else {
            dimensions = ON_GROUND;
        }
        return dimensions.scale(getAgeScale());
    }

    @Override
    public double getFluidJumpThreshold() {
        return 1;
    }

    @Override
    public String getDefaultVariant() {
        return "green";
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.SMALL;
    }

    @Override
    public DragonVariantType<? extends DragonVariant> getVariantType() {
        return URDragonVariantTypes.WYVERN;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().wyvernMaxGroupSize * 2;
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    @Override
    public boolean canGrief() {
        if (!super.canGrief()) return false;
        return isTame() ? URConfig.getConfig().wyvernGriefing.canTamedGrief() : URConfig.getConfig().wyvernGriefing.canUntamedGiref();
    }

    @Override
    public void setTame(boolean isTame, boolean includeSideEffects) {
        super.setTame(isTame, includeSideEffects);
        setCanPickUpLoot(!isTame);
    }

    @Override
    protected void pickUpItem(@NonNull ServerLevel level, ItemEntity entity) {
        if (isFoodItem(entity.getItem())) consumeGivenItem(entity.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null, entity.getItem(), SoundEvents.GENERIC_EAT.value(), null);
    }

    @Override
    public AnimationProcessor<Wyvern> getAnimationProcessor() {
        return processor;
    }
}
