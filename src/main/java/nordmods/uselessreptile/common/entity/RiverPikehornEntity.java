package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.*;
import nordmods.uselessreptile.common.entity.base.URFlyingDragonEntity;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.items.FluteItem;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

public class RiverPikehornEntity extends URFlyingDragonEntity {

    private int glideTimer = 100;
    private boolean shouldGlide;
    private final int huntCooldown = 3000;
    private int huntTimer = huntCooldown;
    public boolean forceTargetInWater = false;
    private final int eatCooldown = 200;
    private int eatTimer = eatCooldown;
    public final net.minecraft.entity.AnimationState blinkAnimation = new net.minecraft.entity.AnimationState();
    public final net.minecraft.entity.AnimationState sitAnimation = new net.minecraft.entity.AnimationState();

    public RiverPikehornEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 5;
        setCanPickUpLoot(true);

        baseSecondaryAttackCooldown = 20;
        basePrimaryAttackCooldown = 20;
        secondaryAttackDuration = 12;
        primaryAttackDuration = 12;
        baseAccelerationDuration = 100;
        rotationSpeedGround = 10;
        rotationSpeedAir = 10;
        canNavigateInFluids = true;
        regenFromFood = 3;
        inventory = new SimpleInventory(0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(IS_HUNTING, false);
    }
    public static final TrackedData<Boolean> IS_HUNTING = DataTracker.registerData(RiverPikehornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean isHunting() {return dataTracker.get(IS_HUNTING);}
    public void setIsHunting (boolean state) {dataTracker.set(IS_HUNTING, state);}

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<RiverPikehornEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::main);
        AnimationController<RiverPikehornEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turn);
        AnimationController<RiverPikehornEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<RiverPikehornEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <A extends GeoEntity> PlayState eye(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState main(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isMoving() || event.isMoving()) {
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.dive", event);
                if (isGliding() || shouldGlide) return loopAnim("fly.glide", event);
                return loopAnim("fly.straight", event);
            }
            event.getController().setAnimationSpeed(Math.max(animationSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving()) return loopAnim("walk", event);
        event.getController().setAnimationSpeed(1);
        if (isDancing()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turn(AnimationState<A> event) {
        byte turnState = getTurningState();
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying() && (isMoving() || event.isMoving()) && !isSecondaryAttack() && !isMovingBackwards()) {
            if (turnState == 1) return loopAnim("turn.fly.left", event);
            if (turnState == 2) return loopAnim("turn.fly.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attack(AnimationState<A> event) {
        event.getController().setAnimationSpeed(1/calcCooldownMod());
        if (isPrimaryAttack()) return playAnim( "attack" + attackType, event);
        return playAnim("attack.none", event);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "flap" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 1, 1.2F);
                case "woosh" -> playSound(URSounds.DRAGON_WOOSH, 0.7f, 1.2f);
                case "step" -> playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.5f, 0.8f);
            }
    }

    private <ENTITY extends GeoEntity> void soundListenerAttack(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            if (event.getKeyframeData().getSound().equals("attack")) playSound(URSounds.PIKEHORN_ATTACK, 1, 1);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return URSounds.PIKEHORN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return URSounds.PIKEHORN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return URSounds.PIKEHORN_DEATH;
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof PlayerEntity) setHitboxModifiers(0.7f, 0.6f, 0);
        else if (isFlying() && isMoving()) setHitboxModifiers(0.6f, 1f, 0);
        else setHitboxModifiers(0.8f, 0.8f, 0);

        dropLootToOwner();
        if (getVehicle() instanceof PlayerEntity player) {
            float yaw = getHeadYaw();
            float centerYaw =  MathHelper.wrapDegrees(yaw - player.bodyYaw);
            float resultYaw = MathHelper.clamp(centerYaw, -90.0F, 90.0F);
            float yawToApply = yaw + resultYaw - centerYaw;
            setHeadYaw(yawToApply);
        }

        if (getWorld().isClient()) {
            glideTimer--;
            shouldGlide = glideTimer < 0 && getAccelerationDuration()/getMaxAccelerationDuration() > 0.9;
            if (glideTimer < -50 - getRandom().nextInt(100)) glideTimer = 100 + getRandom().nextInt(100);
        }

        if (!isTamed()) {
            if (huntTimer > 0 && !isHunting()) huntTimer--;
            else setIsHunting(true);

            if (getMainHandStack().isFood()) {
                if (eatTimer <= 0 || getMaxHealth() > getHealth()) {
                    eatFood(getWorld(), getMainHandStack());
                    heal(getHealthRegenFromFood());
                    stopHunt();
                } else eatTimer--;
            }
        }

        if (isTamed()) {
            PlayerEntity owner = (PlayerEntity) getOwner();
            if (owner != null) {
                ItemStack main = owner.getMainHandStack();
                ItemStack offhand = owner.getOffHandStack();
                boolean mainCanTarget = main.hasNbt() && main.getNbt().getInt(FluteItem.MODE_TAG) == 1;
                boolean offhandCanTarget = offhand.hasNbt() && offhand.getNbt().getInt(FluteItem.MODE_TAG) == 1;
                if (owner.getItemCooldownManager().isCoolingDown(URItems.FLUTE) && (mainCanTarget || offhandCanTarget)) setIsHunting(true);
            }
        }

        if (isInsideWaterOrBubbleColumn()) {
            setSwimming(true);
            setFlying(true);
        }
        else setSwimming(false);
    }

    public static DefaultAttributeContainer.Builder createPikehornAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, getAttributeConfig().pikehornDamage * getAttributeConfig().dragonDamageMultiplier)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, getAttributeConfig().pikehornKnockback * URMobAttributesConfig.getConfig().dragonKnockbackMultiplier)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, getAttributeConfig().pikehornHealth * getAttributeConfig().dragonHealthMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR, getAttributeConfig().pikehornArmor * getAttributeConfig().dragonArmorMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, getAttributeConfig().pikehornArmorToughness * getAttributeConfig().dragonArmorToughnessMultiplier)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, getAttributeConfig().pikehornGroundSpeed * getAttributeConfig().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, getAttributeConfig().pikehornFlyingSpeed * getAttributeConfig().dragonFlyingSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new FlyingDragonCallBackGoal<>(this));
        goalSelector.add(1, new PikehornFluteCallGoal(this));
        goalSelector.add(1, new PikehornFollowGoal(this));
        goalSelector.add(2, new SitGoal(this));
        goalSelector.add(5, new PikehornAttackGoal(this, 4096 * 2));
        goalSelector.add(6, new PikehornHuntGoal(this));
        goalSelector.add(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.add(8, new DragonWanderAroundGoal(this));
        goalSelector.add(8, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.add(9, new DragonLookAroundGoal(this));
        targetSelector.add(3, (new DragonRevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        targetSelector.add(4, new DragonAttackWithOwnerGoal<>(this));
        targetSelector.add(4, new PikehornFluteTargetGoal<>(this, LivingEntity.class));
        targetSelector.add(5, new DragonTrackOwnerAttackerGoal(this));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
            if (!player.isCreative()) player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
            setOwner(player);
            getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            setPersistent();
            return ActionResult.SUCCESS;
        }

        if (isTamed() && isOwnerOrCreative(player)) {
            if (player.isSneaking() && itemStack.isEmpty()) startRiding(player);
        }
        return super.interactMob(player, hand);
    }

    public void attackMelee(LivingEntity target) {
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        attackType = random.nextInt(3)+1;
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        tryAttack(target);
    }

    @Override
    protected void loot(ItemEntity item) {
        if (isOwnerClose()) return;
        if (getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && item.getStack().isIn(ItemTags.FISHES) || getEquippedStack(EquipmentSlot.MAINHAND).isOf(item.getStack().getItem())) {
            triggerItemPickedUpByEntityCriteria(item);
            ItemStack itemStack = item.getStack();
            equipStack(EquipmentSlot.MAINHAND, itemStack);
            updateDropChances(EquipmentSlot.MAINHAND);
            sendPickup(item, itemStack.getCount());
            item.discard();
        }
    }

    @Override
    public boolean hasTargetInWater() {
        return super.hasTargetInWater() || forceTargetInWater;
    }

    @Override
    public int getMaxAir() {
        return 1200;
    }

    public boolean isOwnerClose() {
        LivingEntity owner = getOwner();
        if (owner == null) return false;
        double distance = squaredDistanceTo(owner);
        return distance < getWidth() * 2.0f * (getWidth() * 2.0f);
    }

    private void dropLootToOwner() {
        if (!isTamed() || !isOwnerClose()) return;
        ItemStack stack = getEquippedStack(EquipmentSlot.MAINHAND).copy();
        if (!stack.isEmpty()) {
            dropStack(stack);
            getEquippedStack(EquipmentSlot.MAINHAND).decrement(stack.getCount());
            setIsHunting(false);
        }
    }

    @Override
    public float getRotationSpeed() {
        return super.getRotationSpeed() * (isTouchingWater() ? 4 : 1);
    }

    @Override
    protected float  calcSpeedMod() {
        return super.calcSpeedMod() / (isTouchingWater() ? 2 : 1);
    }

    public void stopHunt() {
        setIsHunting(false);
        huntTimer = huntCooldown + getRandom().nextInt(huntCooldown / 2);
        eatTimer = eatCooldown + getRandom().nextInt(eatCooldown / 2);
        setInAirTimer(getMaxInAirTimer());
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack) {
        return itemStack.isIn(ItemTags.FISHES);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        return itemStack.isOf(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        return super.getPassengerRidingPos(passenger).add(0, 0.275, 0);
    }

    public void updateAnimations() {
        sitAnimation.setRunning(true, age);
        blinkAnimation.setRunning(true, age);
    }
}
