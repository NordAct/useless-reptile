package nordmods.uselessreptile.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.InteractionSettings;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.dragon_variant.type.RiverPikehornVariant;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.FlyingDragonFlyAroundGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.FlyingDragonFlyDownGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.flying.FlyingDragonLookAtEntityGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.RiverPikehornCallBackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.RiverPikehornHuntGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.RiverPikehornHuntTargetGoal;
import nordmods.uselessreptile.common.entity.base.GathererDragon;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.init.URFluteModes;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class RiverPikehorn extends URFlyingDragonEntity implements HeadMountDragon, GathererDragon {
    private static final int HUNT_COOLDOWN = 1200;
    private int huntTimer = getRandom().nextInt(HUNT_COOLDOWN);
    public boolean forceTargetInWater = false;
    private final int eatCooldown = 200;
    private int eatTimer = eatCooldown;
    private boolean isHunting = false;
    private ItemEntity pickupTarget;
    private static final Identifier WATER_SPEED_MODIFIER_BONUS = UselessReptile.id("water_speed_modifier");
    public static final List<FluteItem.FluteMode> FLUTE_MODES = List.of(
            URFluteModes.CALL,
            URFluteModes.TARGET,
            URFluteModes.GATHER,
            URFluteModes.SIT_DOWN,
            URFluteModes.STAND_UP
    );

    public RiverPikehorn(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        xpReward = 5;
        setCanPickUpLoot(true);
        ticksUntilHeal = 400;
        setYawTargetLimit(45);
        setPitchTargetLimit(45);
    }

    public boolean isHunting() {
        return isHunting;
    }
    public void setHunting(boolean state) {
        isHunting = state;
    }

    public void tickAnimations() {
        if (!level().isClientSide()) return;
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
        if (isPassenger()) {
            mainController.playAnimation("sit.head");
            return;
        }
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
        if (isMoving()) {
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

        if (!isTame() && level() instanceof ServerLevel world) {
            if (!isHunting() && --huntTimer <= 0) setHunting(true);

            ItemStack itemStack = getOffhandItem();
            if (!itemStack.isEmpty() && --eatTimer <= 0) {
                InteractionSettings.FoodItem foodItem = getFoodItem(itemStack);
                if (foodItem != null) {
                    heal(foodItem.healingAmount());
                    setItemSlot(EquipmentSlot.OFFHAND, consumeGivenItem(this, itemStack, SoundEvents.GENERIC_EAT.value(), null));
                } else spawnAtLocation(world, itemStack);
                stopGathering();
            }

            getItemBySlot(EquipmentSlot.OFFHAND);
        }

        if (!level().isClientSide()) {
            if (isUnderWater()) {
                setSwimming(true);
                setFlying(true);
            } else setSwimming(false);
        }

        if (level() instanceof ServerLevel world) dropLootToOwner(world);
        tickAnimations();
    }

    @Override
    public void updateMovementModifiers() {
        AttributeInstance instance = isFlying() ?getAttribute(Attributes.FLYING_SPEED) : getAttribute(Attributes.MOVEMENT_SPEED);
        if (isUnderWater()) {
            AttributeModifier modifier = new AttributeModifier(WATER_SPEED_MODIFIER_BONUS, -0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            if (!instance.hasModifier(modifier.id())) instance.addTransientModifier(modifier);
            else instance.addOrUpdateTransientModifier(modifier);
        } else instance.removeModifier(WATER_SPEED_MODIFIER_BONUS);

        super.updateMovementModifiers();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RiverPikehornCallBackGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(5, new DragonAttackGoal(this));
        goalSelector.addGoal(6, new RiverPikehornHuntGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonReturnToHomePoint<>(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal<>(this));
        goalSelector.addGoal(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(10, new FlyingDragonLookAtEntityGoal<>(this, LivingEntity.class, 16));
        goalSelector.addGoal(10, new DragonLookAroundGoal<>(this));
        goalSelector.addGoal(11, new DragonLookAtPathTartetGoal<>(this));
        targetSelector.addGoal(1, new DragonRevengeGoal<>(this).setAlertOthers());
        targetSelector.addGoal(2, new DragonOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DragonOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new DragonNonTameRandomTargetGoal(this, true));
        targetSelector.addGoal(4, new DragonTameRandomTargetGoal(this, true));
        targetSelector.addGoal(5, new RiverPikehornHuntTargetGoal(this));
    }

    @Override
    protected void pickUpItem(@NonNull ServerLevel world, @NonNull ItemEntity item) {
        if (isOwnerClose()) return;
        if (getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && isFood(item.getItem())
                || getItemBySlot(EquipmentSlot.OFFHAND).is(item.getItem().getItem()) && item.getItem().getComponents().equals(getItemBySlot(EquipmentSlot.OFFHAND).getComponents())) {
            onItemPickup(item);
            ItemStack itemStack = item.getItem();
            setItemSlot(EquipmentSlot.OFFHAND, itemStack);
            setGuaranteedDrop(EquipmentSlot.OFFHAND);
            take(item, itemStack.getCount());
            item.discard();
            setPickupTarget(null);
        }
    }

    @Override
    public boolean hasTargetInWater() {
        return (super.hasTargetInWater() || forceTargetInWater) && getAirSupply() > getMaxAirSupply() * 0.2;
    }

    @Override
    public boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public int getMaxAirSupply() {
        return 1200;
    }

    public boolean isOwnerClose() {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        double distance = distanceToSqr(owner);
        return distance < getBbWidth() * 2.0f * (getBbWidth() * 2.0f);
    }

    private void dropLootToOwner(ServerLevel world) {
        if (!isTame() || !isOwnerClose() || level().isClientSide()) return;
        ItemStack stack = getItemBySlot(EquipmentSlot.OFFHAND).copy();
        if (!stack.isEmpty()) {
            ItemEntity item = spawnAtLocation(world, stack);
            if (item != null) item.setDeltaMovement(getOwner().position().subtract(position()).normalize().scale(0.2));
            getItemBySlot(EquipmentSlot.OFFHAND).shrink(stack.getCount());
            setHunting(false);
        }
    }

    public void stopGathering() {
        setHunting(false);
        huntTimer = HUNT_COOLDOWN + getRandom().nextInt(HUNT_COOLDOWN / 2);
        eatTimer = eatCooldown + getRandom().nextInt(eatCooldown / 2);
        setInAirTimer(getMaxInAirTimer());
    }

    @Override
    public String getDefaultVariant() {
        return "blue";
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.NONE;
    }

    @Override
    public DragonVariantType<? extends DragonVariant> getVariantType() {
        return URDragonVariantTypes.RIVER_PIKEHORN;
    }

    @Override
    public @NonNull Vec3 getVehicleAttachmentPoint(@NonNull Entity vehicle) {
        return super.getVehicleAttachmentPoint(vehicle).add(0, vehicle.getBbHeight() - vehicle.getEyeHeight(vehicle.getPose()) - 0.001, 0);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().riverPikehornMaxGroupSize * 2;
    }

    @Override
    public boolean doesEmitEquipEvent(@NonNull EquipmentSlot slot) {
        return slot != EquipmentSlot.OFFHAND;
    }

    @Override
    public void startGathering() {
        setHunting(true);
    }

    @Override
    public List<FluteItem.FluteMode> getPermittedFluteModes() {
        return FLUTE_MODES;
    }

    @Nullable
    public ItemEntity getPickupTarget() {
        return pickupTarget;
    }

    public void setPickupTarget(@Nullable ItemEntity pickupTarget) {
        this.pickupTarget = pickupTarget;
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel world, @NonNull DamageSource damageSource, float amount) {
        if (isHunting() && damageSource.getEntity() != null) {
            huntTimer = HUNT_COOLDOWN;
            setHunting(false);
        }
        return super.hurtServer(world, damageSource, amount);
    }

    public boolean isHuntingTarget(LivingEntity entity) {
        return ((RiverPikehornVariant)getDragonVariant()).huntTargets().stream().anyMatch(entry -> {
            if (entry.tag()) return entity.is(TagKey.create(Registries.ENTITY_TYPE, entry.id()));
            else return entity.is(ResourceKey.create(Registries.ENTITY_TYPE, entry.id()));
        });
    }

    @Override
    public boolean canGrief() {
        if (!super.canGrief()) return false;
        return isTame() ? URConfig.getConfig().riverPikehornGriefing.canTamedGrief() : URConfig.getConfig().riverPikehornGriefing.canUntamedGiref();
    }
}
