package nordmods.uselessreptile.common.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.CommonDragonVariantData;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornCallBackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornHuntGoal;
import nordmods.uselessreptile.common.entity.base.GathererDragon;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.init.URFluteModes;
import nordmods.uselessreptile.common.item.FluteItem;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RiverPikehorn extends URFlyingDragonEntity implements HeadMountDragon, GathererDragon {
    private final int huntCooldown = 1200;
    private int huntTimer = getRandom().nextInt(huntCooldown);
    public boolean forceTargetInWater = false;
    private final int eatCooldown = 200;
    private int eatTimer = eatCooldown;
    private boolean isHunting = false;

    private static final Identifier WATER_SPEED_MODIFIER_BONUS = UselessReptile.id("water_speed_modifier");

    public static final float BASE_GROUND_SPEED = 0.2f;
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

        secondaryAttackDuration = 11;
        primaryAttackDuration = 11;
        ticksUntilHeal = 400;
    }

    public boolean isHunting() {
        return isHunting;
    }
    public void setHunting(boolean state) {
        isHunting = state;
    }

    //todo reconsider structure and make it cleaner
    public void tickAnimations() {
        if (!level().isClientSide()) return;
        tickBlinkController();
        tickTurnController();
        tickMainController();
    }

    private void tickBlinkController() {
        URDragonAnimationController<URDragonEntity> blinkController = getAnimationController(AnimationController.BLINK);
        if (blinkController.isPlayingAbilityAnimation(AnimationController.BLINK)) return;
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickTurnController() {
        URDragonAnimationController<URDragonEntity> turnController = getAnimationController(AnimationController.TURN);
        if (turnController.isPlayingAbilityAnimation(AnimationController.TURN)) return;
        switch (getTurningState()) {
            case LEFT -> {
                if (isFlying() && isMoving() && !isMovingBackwards()) turnController.playAnimation("turn.fly.left");
                else turnController.playAnimation("turn.left");
            }
            case RIGHT -> {
                if (isFlying() && isMoving() && !isMovingBackwards()) turnController.playAnimation("turn.fly.right");
                else turnController.playAnimation("turn.right");
            }
            default -> turnController.getPlayingAnimations().forEach(BRPlayingAnimation::stop);
        }
    }

    private void tickMainController() {
        URDragonAnimationController<URDragonEntity> mainController = getAnimationController(AnimationController.MAIN);
        float animationSpeed = getMovementSpeedModifier();
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(animationSpeed));
        if (mainController.isPlayingAbilityAnimation(AnimationController.MAIN)) return;
        if (isPassenger()) {
            mainController.playAnimation("sit.head");
            return;
        }
        if (isFlying()) {
            if (isMoving()) {
                if (getTiltState() == TiltState.UP) {
                    mainController.playAnimation("fly.straight.up");
                    return;
                }
                if (getTiltState() == TiltState.DOWN) {
                    mainController.playAnimation("fly.dive");
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
        if (isMoving()) {
            mainController.playAnimation("walk");
            return;
        }
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(Math.max(animationSpeed, 1)));
        if (isDancing()) {
            mainController.playAnimation("dance");
            return;
        }
        mainController.playAnimation("idle");
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof Player) setHitboxModifiers(0.7f, 0.6f, 0);
        else setHitboxModifiers(0.7f, 0.8f, 0);

        if (!isTame() && level() instanceof ServerLevel world) {
            if (!isHunting() && --huntTimer <= 0) setHunting(true);

            ItemStack itemStack = getOffhandItem();
            if (!itemStack.isEmpty() && --eatTimer <= 0) {
                CommonDragonVariantData.FoodItem foodItem = getFoodItem(itemStack);
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
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    public static AttributeSupplier.Builder createPikehornAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().riverPikehornDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().riverPikehornKnockback )
                .add(Attributes.MAX_HEALTH, attributes().riverPikehornHealth)
                .add(Attributes.ARMOR, attributes().riverPikehornArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().riverPikehornArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().riverPikehornGroundSpeed)
                .add(Attributes.FLYING_SPEED, attributes().riverPikehornFlyingSpeed)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().riverPikehornVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().riverPikehornBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().riverPikehornRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().riverPikehornRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().riverPikehornBasePrimaryAttackCooldown);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new PikehornCallBackGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(5, new PikehornAttackGoal(this, 4096 * 2));
        goalSelector.addGoal(6, new PikehornHuntGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(10, new DragonLookAroundGoal(this));
        targetSelector.addGoal(3, (new DragonRevengeGoal(this)).setAlertOthers());
        targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(5, new OwnerHurtByTargetGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
    }

    public void attackMelee(LivingEntity target) {
        if (!(level() instanceof ServerLevel world)) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        doHurtTarget(world, target);
    }

    @Override
    protected void pickUpItem(@NonNull ServerLevel world, @NonNull ItemEntity item) {
        if (isOwnerClose()) return;
        if (getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && getFoodItem(item.getItem()) != null
                || getItemBySlot(EquipmentSlot.OFFHAND).is(item.getItem().getItem()) && item.getItem().getComponents().equals(getItemBySlot(EquipmentSlot.OFFHAND).getComponents())) {
            onItemPickup(item);
            ItemStack itemStack = item.getItem();
            setItemSlot(EquipmentSlot.OFFHAND, itemStack);
            setGuaranteedDrop(EquipmentSlot.OFFHAND);
            take(item, itemStack.getCount());
            item.discard();
        }
    }

    @Override
    public boolean hasTargetInWater() {
        return super.hasTargetInWater() || forceTargetInWater;
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

    @Override
    public int getHeadRotSpeed() {
        return (int) (super.getHeadRotSpeed() * (isInWater() ? 2f : 1f));
    }

    public void stopGathering() {
        setHunting(false);
        huntTimer = huntCooldown + getRandom().nextInt(huntCooldown / 2);
        eatTimer = eatCooldown + getRandom().nextInt(eatCooldown / 2);
        setInAirTimer(getMaxInAirTimer());
    }

    @Override
    public @NonNull AABB getPrimaryAttackBox() {
        return getBoundingBox().inflate(getScale(), 0, getScale());
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
}
