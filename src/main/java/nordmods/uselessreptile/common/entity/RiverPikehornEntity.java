package nordmods.uselessreptile.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornFluteCallGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornFollowGoal;
import nordmods.uselessreptile.common.entity.ai.goal.river_pikehorn.PikehornHuntGoal;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URFlyingDragonEntity;
import nordmods.uselessreptile.common.init.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;

import java.util.function.BiConsumer;

public class RiverPikehornEntity extends URFlyingDragonEntity implements HeadMountDragon {
    private final int huntCooldown = 1200;
    private int huntTimer = getRandom().nextInt(huntCooldown);
    public boolean forceTargetInWater = false;
    private final int eatCooldown = 200;
    private int eatTimer = eatCooldown;
    private boolean isHunting = false;
    protected final EntityGameEventHandler<FluteUsedEventListener> fluteUsedEventHandler = new EntityGameEventHandler<>(new FluteUsedEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), URGameEvents.FLUTE_USED.value().notificationRadius()));

    public static float BASE_GROUND_SPEED = 0.2f;

    public RiverPikehornEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 5;
        setCanPickUpLoot(true);

        secondaryAttackDuration = 12;
        primaryAttackDuration = 12;
        canNavigateInFluids = true;
        inventory = new SimpleInventory(0);
        ticksUntilHeal = 400;
    }

    public boolean isHunting() {
        return isHunting;
    }
    public void setIsHunting (boolean state) {
        isHunting = state;
    }

    @Override
    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        if (getWorld() instanceof ServerWorld serverWorld) callback.accept(fluteUsedEventHandler, serverWorld);
        super.updateEventHandler(callback);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<RiverPikehornEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::mainController);
        AnimationController<RiverPikehornEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turnController);
        AnimationController<RiverPikehornEntity> attack = new AnimationController<>(this, "attack", 0, this::attackController);
        AnimationController<RiverPikehornEntity> eye = new AnimationController<>(this, "eye", 0, this::eyeController);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <A extends GeoEntity> PlayState eyeController(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState mainController(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animationSpeed);
        if (hasVehicle()) return loopAnim("sit.head", event);
        if (isFlying()) {
            if (isMoving() || event.isMoving()) {
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.dive", event);
                if (shouldGlide) return loopAnim("fly.glide", event);
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

    private <A extends GeoEntity> PlayState turnController(AnimationState<A> event) {
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

    private <A extends GeoEntity> PlayState attackController(AnimationState<A> event) {
        event.getController().setAnimationSpeed(1/ getCooldownModifier());
        if (isPrimaryAttack()) return playAnim( "attack" + getAttackType(), event);
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
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (getVehicle() instanceof PlayerEntity && damageSource.isOf(DamageTypes.IN_WALL)) return true;
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof PlayerEntity) setHitboxModifiers(0.7f, 0.6f, 0);
        else setHitboxModifiers(0.7f, 0.8f, 0);

        if (!isTamed() && !getWorld().isClient()) {
            if (!isHunting() && --huntTimer <= 0) setIsHunting(true);

            ItemStack itemStack = getMainHandStack();
            if (!itemStack.isEmpty() && --eatTimer <= 0) {
                if (isFavoriteFood(itemStack)) {
                    heal(getHealthRegenerationFromFood());
                    equipStack(EquipmentSlot.MAINHAND, consumeGivenItem(this, itemStack, SoundEvents.ENTITY_GENERIC_EAT));
                } else dropStack(itemStack);
                stopHunt();
            }

            getEquippedStack(EquipmentSlot.MAINHAND);
        }
        setSpeedMod(isInsideWaterOrBubbleColumn() ? 0.5f : 1);
        if (!getWorld().isClient()) {
            if (isInsideWaterOrBubbleColumn()) {
                setSwimming(true);
                setFlying(true);
            } else setSwimming(false);
        }

        dropLootToOwner();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    public static DefaultAttributeContainer.Builder createPikehornAttributes() {
        return createDragonAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attributes().riverPikehornDamage)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, attributes().riverPikehornKnockback )
                .add(EntityAttributes.GENERIC_MAX_HEALTH, attributes().riverPikehornHealth)
                .add(EntityAttributes.GENERIC_ARMOR, attributes().riverPikehornArmor)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, attributes().riverPikehornArmorToughness)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, attributes().riverPikehornGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, attributes().riverPikehornFlyingSpeed * attributes().dragonFlyingSpeedMultiplier)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().riverPikehornVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().riverPikehornBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().riverPikehornRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().riverPikehornRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().riverPikehornBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().riverPikehornRegenerationFromFood);
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
        targetSelector.add(4, new DragonAttackWithOwnerGoal(this));
        targetSelector.add(5, new DragonTrackOwnerAttackerGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.add(4, new UntamedActiveTargetGoal<>(this, PlayerEntity.class, true, null));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
            player.setStackInHand(hand, consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT));
            setOwner(player);
            getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            setPersistent();
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    public void attackMelee(LivingEntity target) {
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        tryAttack(target);
    }

    @Override
    protected void loot(ItemEntity item) {
        if (isOwnerClose()) return;
        if (getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && isFavoriteFood(item.getStack())
                || getEquippedStack(EquipmentSlot.MAINHAND).isOf(item.getStack().getItem()) && item.getStack().getComponents().equals(getEquippedStack(EquipmentSlot.MAINHAND).getComponents())) {
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
        if (!isTamed() || !isOwnerClose() || getWorld().isClient()) return;
        ItemStack stack = getEquippedStack(EquipmentSlot.MAINHAND).copy();
        if (!stack.isEmpty()) {
            dropStack(stack);
            getEquippedStack(EquipmentSlot.MAINHAND).decrement(stack.getCount());
            setIsHunting(false);
        }
    }

    @Override
    public float getRotationSpeed() {
        return super.getRotationSpeed() * (isTouchingWater() ? 2f : 1f);
    }

    public void stopHunt() {
        setIsHunting(false);
        huntTimer = huntCooldown + getRandom().nextInt(huntCooldown / 2);
        eatTimer = eatCooldown + getRandom().nextInt(eatCooldown / 2);
        setInAirTimer(getMaxInAirTimer());
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack) {
        return itemStack.isIn(URTags.RIVER_PIKEHORN_FOOD);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.RIVER_PIKEHORN_TAMING_ITEM);
    }

    @Override
    public Box getAttackBox() {
        return getBoundingBox().expand(getScale(), 0, getScale());
    }

    @Override
    public String getDefaultVariant() {
        return "blue";
    }

    @Override
    public Vec3d getVehicleAttachmentPos(Entity vehicle) {
        return super.getVehicleAttachmentPos(vehicle).add(0, vehicle.getHeight() - vehicle.getEyeHeight(vehicle.getPose()) - 0.001, 0);
    }

    @Override
    public int getLimitPerChunk() {
        return URConfig.getConfig().riverPikehornMaxGroupSize * 2;
    }

    @Override
    public boolean isArmorSlot(EquipmentSlot slot) {
        return slot != EquipmentSlot.MAINHAND;
    }

    protected class FluteUsedEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public FluteUsedEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public PositionSource getPositionSource() {return this.positionSource;}

        public int getRange() {return this.range;}

        @Override
        public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            if (event != URGameEvents.FLUTE_USED) return false;
            if (!(emitter.sourceEntity() instanceof PlayerEntity player)) return false;
            if (getOwner() != player) return false;

            ItemStack stack = player.getMainHandStack();
            if (!stack.isOf(URItems.FLUTE)) stack = player.getOffHandStack();
            if (!stack.isOf(URItems.FLUTE)) return false;

            switch (URItems.FLUTE.getFluteMode(stack)) {
                default -> shouldFollow = true;
                case 1 -> setIsHunting(true);
                case 2 -> {
                    Vec3d rot = player.getRotationVec(1);
                    EntityHitResult hitResult = ProjectileUtil
                            .raycast(player,
                                    player.getCameraPosVec(1),
                                    player.getCameraPosVec(1).add(rot.multiply(range)),
                                    player.getBoundingBox().stretch(rot.multiply(range)).expand(1.0, 1.0, 1.0),
                                    entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.canHit(), range * range);

                    if (hitResult != null) setTarget((LivingEntity) hitResult.getEntity());
                }
            }

            return true;
        }
    }
}
