package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_ability.holder.DragonAbilityHolder;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.wyvern.WyvernAttackGoal;
import nordmods.uselessreptile.common.entity.animation_processor.DragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.animation_processor.MultipartDragonAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.MultipartDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.s2c.SyncEntityPartsPosPayload;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class Wyvern extends URRideableFlyingDragonEntity implements MultipartDragon {
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
    private final URDragonPart[] parts = new URDragonPart[]{head, neck1, neck2, neck3, neck4, neck5, front, back, tail1, tail2, tail3, tail4, tail5};
    private List<Vec3> nextPoses = List.of();
    private static final EntityDimensions FLYING_IDLE = EntityDimensions.scalable(2.95f, 2.95f).withEyeHeight(2.9f);
    private static final EntityDimensions FLYING_FORWARD = EntityDimensions.scalable(2.95f, 1).withEyeHeight(0.9f);
    private static final EntityDimensions ON_GROUND = EntityDimensions.scalable(1.8f, 2.95f).withEyeHeight(2.9f);

    public static final float BASE_GROUND_SPEED = 0.2f;

    public Wyvern(EntityType<? extends URRideableFlyingDragonEntity> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;

        pitchLimitGround = 80;
        pitchLimitAir = 45;
        ticksUntilHeal = 200;
        sprintSpeedModifier = 1.2f;
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
        goalSelector.addGoal(6, new WyvernAttackGoal(this, 512));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(10, new DragonLookAroundGoal(this));
        targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Chicken.class, true, null));
        targetSelector.addGoal(5, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(4, new DragonRevengeGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
    }

    public static AttributeSupplier.Builder createWyvernAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().wyvernDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().wyvernKnockback)
                .add(Attributes.MAX_HEALTH, attributes().wyvernHealth)
                .add(Attributes.ARMOR, attributes().wyvernArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().wyvernArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().wyvernGroundSpeed)
                .add(Attributes.FLYING_SPEED, attributes().wyvernFlyingSpeed)
                .add(Attributes.JUMP_STRENGTH, 0.42 * 1.5)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().wyvernVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().wyvernBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().wyvernRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().wyvernRotationSpeedAir);
    }

    //todo reconsider structure and make it cleaner
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
                if (getTiltState() == TiltState.DOWN && getMovementSpeedModifier() > 0.25 && getXBodyRot(1) > 10) {
                    mainController.playAnimation("fly.down");
                    return;
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
    public boolean canBeAffected(MobEffectInstance effect) {
        Holder<MobEffect> type = effect.getEffect();
        return !(type == URMobEffect.ACID || type == MobEffects.POISON || type == MobEffects.HUNGER);
    }

    @Override
    public void tick() {
        if (level().isClientSide()) {
            for (int i = 0; i < nextPoses.size(); i++) {
                EntityPart part = getParts()[i];
                part.setOldPosAndRot();
                part.setPos(nextPoses.get(i));
            }
        }
        tickAnimations();
        super.tick();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public @NonNull InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame()) {
            if (itemStack.getItem() == Items.GLASS_BOTTLE && isOwnedBy(player)) {
                Item bottle = itemStack.getItem();
                ItemStack potion = new ItemStack(Items.POTION);
                potion.set(DataComponents.POTION_CONTENTS, new PotionContents(URPotions.ACID));
                player.awardStat(Stats.ITEM_USED.get(bottle));
                level().playPlayerSound(SoundEvents.BOTTLE_FILL, player.getSoundSource(), 1.0F, 1.0F);
                consumeGivenItem(player, itemStack, SoundEvents.BOTTLE_FILL, hand);
                player.addItem(potion);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
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

    public void shoot() { //todo remove
        getAvailableAbilities()
                .stream()
                .filter(a -> a.getAbility().getType().equals(URDragonAbilityTypes.SHOT_ATTACK))
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
        double modifier = isFlying() ? getBbWidth() / 2 : (getBbWidth() + 0.1);
        double x = -Math.sin(Math.toRadians(getYRot())) * modifier;
        double z = Math.cos(Math.toRadians(getYRot())) * modifier;
        double y = isFlying() ? -2 : 0;
        return new AABB(position().x() + x - getBbWidth() / 1.5, position().y() + y, position().z() + z - getBbWidth() / 1.5,
                position().x() + x + getBbWidth() / 1.5, position().y() + getBbHeight() + 1, position().z() + z + getBbWidth() / 1.5);
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
    public float getHeightModTransSpeed() {
        return (float) (0.13 * getScale());
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
    public void sendSyncPayload() {
        if (level() instanceof ServerLevel serverWorld)
            for (ServerPlayer player : PlayerLookup.tracking(serverWorld, blockPosition()))
                SyncEntityPartsPosPayload.send(player, this);
    }

    @Override
    public void handleSyncPayload(SyncEntityPartsPosPayload payload) {
        nextPoses = payload.poses();
    }

    @Override
    public @Nullable DragonAnimationProcessor<Wyvern> createServerAnimationProcessor() {
        return new MultipartDragonAnimationProcessor<>(this);
    }
}
