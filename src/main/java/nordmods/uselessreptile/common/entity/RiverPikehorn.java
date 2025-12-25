package nordmods.uselessreptile.common.entity;

import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornFluteCallGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornFollowGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornHuntGoal;
import nordmods.uselessreptile.common.entity.base.FluteListener;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.item.FluteItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class RiverPikehorn extends URFlyingDragonEntity implements HeadMountDragon, FluteListener {
    private final int huntCooldown = 1200;
    private int huntTimer = getRandom().nextInt(huntCooldown);
    public boolean forceTargetInWater = false;
    private final int eatCooldown = 200;
    private int eatTimer = eatCooldown;
    private boolean isHunting = false;
    protected final DynamicGameEventListener<FluteUsedEventListener> fluteUsedEventHandler = new DynamicGameEventListener<>(new FluteUsedEventListener
            (new EntityPositionSource(this, getEyeHeight()), URGameEvents.FLUTE_USED.value().notificationRadius()));
    private static final Identifier WATER_SPEED_MODIFIER_BONUS = UselessReptile.id("water_speed_modifier");
    public static final float BASE_GROUND_SPEED = 0.2f;

    public RiverPikehorn(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        xpReward = 5;
        setCanPickUpLoot(true);

        secondaryAttackDuration = 12;
        primaryAttackDuration = 12;
        canNavigateInFluids = true;
        ticksUntilHeal = 400;
    }

    public boolean isHunting() {
        return isHunting;
    }
    public void setIsHunting (boolean state) {
        isHunting = state;
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
        if (level() instanceof ServerLevel serverWorld) callback.accept(fluteUsedEventHandler, serverWorld);
        super.updateDynamicGameEventListener(callback);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return null;
    }
    //todo
//    @Override
//    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
//        AnimationController<RiverPikehorn> main = new AnimationController<>("main", TRANSITION_TICKS, this::mainController);
//        AnimationController<RiverPikehorn> turn = new AnimationController<>( "turn", TRANSITION_TICKS, this::turnController);
//        AnimationController<RiverPikehorn> attack = new AnimationController<>("attack", 0, this::attackController);
//        AnimationController<RiverPikehorn> eye = new AnimationController<>("eye", 0, this::eyeController);
//        main.setSoundKeyframeHandler(this::soundHandler);
//        attack.setSoundKeyframeHandler(this::soundHandler);
//        turn.setSoundKeyframeHandler(this::soundHandler);
//        eye.setSoundKeyframeHandler(this::soundHandler);
//        animationData.add(main, turn, attack, eye);
//    }
//
//    private <A extends GeoEntity> PlayState eyeController(AnimationTest<A> event) {
//        return loopAnim("blink", event);
//    }
//    private <A extends GeoEntity> PlayState mainController(AnimationTest<A> event) {
//        event.controller().transitionLength((int) (TRANSITION_TICKS / event.controller().getAnimationSpeed()));
//        event.controller().setAnimationSpeed(animationSpeed);
//        if (isPassenger()) return loopAnim("sit.head", event);
//        if (isFlying()) {
//            if (isMoving() || event.isMoving()) {
//                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
//                if (getTiltState() == 2) return loopAnim("fly.dive", event);
//                if (isFlyGliding()) return loopAnim("fly.glide", event);
//                return loopAnim("fly.straight", event);
//            }
//            event.controller().setAnimationSpeed(Math.max(animationSpeed, 1));
//            return loopAnim("fly.idle", event);
//        }
//        if (isOrderedToSit() && !isDancing()) return loopAnim("sit", event);
//        if (event.isMoving()) return loopAnim("walk", event);
//        event.controller().setAnimationSpeed(1);
//        if (isDancing()) return loopAnim("dance", event);
//        return loopAnim("idle", event);
//    }
//
//    private <A extends GeoEntity> PlayState turnController(AnimationTest<A> event) {
//        byte turnState = getTurningState();
//        if (isFlying() && (isMoving() || event.isMoving()) && !isSecondaryAttack() && !isMovingBackwards()) {
//            if (turnState == 1) return loopAnim("turn.fly.left", event);
//            if (turnState == 2) return loopAnim("turn.fly.right", event);
//        }
//        if (turnState == 1) return loopAnim("turn.left", event);
//        if (turnState == 2) return loopAnim("turn.right", event);
//        return loopAnim("turn.none", event);
//    }
//
//    private <A extends GeoEntity> PlayState attackController(AnimationTest<A> event) {
//        event.controller().setAnimationSpeed(1/ getCooldownModifier());
//        if (isPrimaryAttack()) return playAnim( "attack" + getAttackType(), event);
//        return playAnim("attack.none", event);
//    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof Player) setHitboxModifiers(0.7f, 0.6f, 0);
        else setHitboxModifiers(0.7f, 0.8f, 0);

        if (!isTame() && level() instanceof ServerLevel world) {
            if (!isHunting() && --huntTimer <= 0) setIsHunting(true);

            ItemStack itemStack = getMainHandItem();
            if (!itemStack.isEmpty() && --eatTimer <= 0) {
                DragonVariant.FoodItem foodItem = getFoodItem(itemStack);
                if (foodItem != null) {
                    heal(foodItem.healingAmount());
                    setItemSlot(EquipmentSlot.MAINHAND, consumeGivenItem(this, itemStack, SoundEvents.GENERIC_EAT.value(), null));
                } else spawnAtLocation(world, itemStack);
                stopHunt();
            }

            getItemBySlot(EquipmentSlot.MAINHAND);
        }

        if (!level().isClientSide()) {
            if (isUnderWater()) {
                setSwimming(true);
                setFlying(true);
            } else setSwimming(false);
        }

        if (level() instanceof ServerLevel world) dropLootToOwner(world);
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
        goalSelector.addGoal(1, new FlyingDragonCallBackGoal<>(this));
        goalSelector.addGoal(1, new PikehornFluteCallGoal(this));
        goalSelector.addGoal(1, new PikehornFollowGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(5, new PikehornAttackGoal(this, 4096 * 2));
        goalSelector.addGoal(6, new PikehornHuntGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(8, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(9, new DragonLookAroundGoal(this));
        targetSelector.addGoal(3, (new DragonRevengeGoal(this, new Class[0])).setAlertOthers(new Class[0]));
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
    protected void pickUpItem(ServerLevel world, ItemEntity item) {
        if (isOwnerClose()) return;
        if (getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && getFoodItem(item.getItem()) != null
                || getItemBySlot(EquipmentSlot.MAINHAND).is(item.getItem().getItem()) && item.getItem().getComponents().equals(getItemBySlot(EquipmentSlot.MAINHAND).getComponents())) {
            onItemPickup(item);
            ItemStack itemStack = item.getItem();
            setItemSlot(EquipmentSlot.MAINHAND, itemStack);
            setGuaranteedDrop(EquipmentSlot.MAINHAND);
            take(item, itemStack.getCount());
            item.discard();
        }
    }

    @Override
    public boolean hasTargetInWater() {
        return super.hasTargetInWater() || forceTargetInWater;
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
        ItemStack stack = getItemBySlot(EquipmentSlot.MAINHAND).copy();
        if (!stack.isEmpty()) {
            ItemEntity item = spawnAtLocation(world, stack);
            if (item != null) item.setDeltaMovement(getOwner().position().subtract(position()).normalize().scale(0.2));
            getItemBySlot(EquipmentSlot.MAINHAND).shrink(stack.getCount());
            setIsHunting(false);
        }
    }

    @Override
    public float getRotationSpeed() {
        return super.getRotationSpeed() * (isInWater() ? 2f : 1f);
    }

    public void stopHunt() {
        setIsHunting(false);
        huntTimer = huntCooldown + getRandom().nextInt(huntCooldown / 2);
        eatTimer = eatCooldown + getRandom().nextInt(eatCooldown / 2);
        setInAirTimer(getMaxInAirTimer());
    }

    @Override
    public @NotNull AABB getPrimaryAttackBox() {
        return getBoundingBox().inflate(getScale(), 0, getScale());
    }

    @Override
    public String getDefaultVariant() {
        return "blue";
    }

    @Override
    public boolean isSaddle(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isHelmet(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isChestplate(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isTailArmor(ItemStack itemStack) {
        return false;
    }

    @Override
    public @NotNull DragonInventory createInventory() {
        return new DragonInventory(this, DragonInventory.StorageSize.NO_INVENTORY, false, false, false);
    }

    @Override
    public @NotNull Vec3 getVehicleAttachmentPoint(Entity vehicle) {
        return super.getVehicleAttachmentPoint(vehicle).add(0, vehicle.getBbHeight() - vehicle.getEyeHeight(vehicle.getPose()) - 0.001, 0);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().riverPikehornMaxGroupSize * 2;
    }

    @Override
    public boolean doesEmitEquipEvent(EquipmentSlot slot) {
        return slot != EquipmentSlot.MAINHAND;
    }

    @Override
    public void startGathering() {
        setIsHunting(true);
    }

    @Override
    public void respondToFlute(FluteItem.FluteAction action) {
        action.run(this);
    }

    @Override
    public Collection<BRAnimationController<?>> getAnimationControllers() {
        return List.of();
    }

    protected class FluteUsedEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public FluteUsedEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NotNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(ServerLevel world, Holder<GameEvent> event, GameEvent.Context emitter, Vec3 emitterPos) {
            if (event != URGameEvents.FLUTE_USED) return false;
            if (!(emitter.sourceEntity() instanceof Player player)) return false;
            if (getOwner() != player) return false;

            ItemStack stack = player.getMainHandItem();
            if (!stack.is(URItems.FLUTE)) stack = player.getOffhandItem();
            if (!stack.is(URItems.FLUTE)) return false;

            respondToFlute(FluteItem.getFluteModeAction(stack));

            return true;
        }
    }
}
