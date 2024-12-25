package nordmods.uselessreptile.common.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.wyvern.WyvernAttackGoal;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.URPacketHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.keyframe.event.SoundKeyframeEvent;

import java.util.List;

public class WyvernEntity extends URRideableFlyingDragonEntity implements MultipartEntity {

    private final URDragonPart wingLeft = new URDragonPart(this);
    private final URDragonPart wingRight = new URDragonPart(this);
    private final URDragonPart neck = new URDragonPart(this);
    private final URDragonPart head = new URDragonPart(this);
    private final URDragonPart tail1 = new URDragonPart(this);
    private final URDragonPart tail2 = new URDragonPart(this);
    private final URDragonPart tail3 = new URDragonPart(this);
    private final URDragonPart[] parts = new URDragonPart[]{wingLeft, wingRight, neck, head, tail1, tail2, tail3};

    public static float BASE_GROUND_SPEED = 0.2f;

    public WyvernEntity(EntityType<? extends URRideableFlyingDragonEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        baseTamingProgress = 128;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        ticksUntilHeal = 200;
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.add(3, new SitGoal(this));
        goalSelector.add(4, new DragonConsumeFoodFromInventoryGoal(this));
        goalSelector.add(6, new WyvernAttackGoal(this, 512));
        goalSelector.add(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.add(8, new DragonReturnToHomePoint(this));
        goalSelector.add(9, new DragonWanderAroundGoal(this));
        goalSelector.add(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.add(10, new DragonLookAroundGoal(this));
        targetSelector.add(6, new UntamedActiveTargetGoal<>(this, ChickenEntity.class, true, null));
        targetSelector.add(5, new DragonAttackWithOwnerGoal(this));
        targetSelector.add(4, new DragonRevengeGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.add(5, new UntamedActiveTargetGoal<>(this, PlayerEntity.class, true, null));
    }

    public static DefaultAttributeContainer.Builder createWyvernAttributes() {
        return createDragonAttributes()
                .add(EntityAttributes.ATTACK_DAMAGE, attributes().wyvernDamage)
                .add(EntityAttributes.ATTACK_KNOCKBACK, attributes().wyvernKnockback)
                .add(EntityAttributes.MAX_HEALTH, attributes().wyvernHealth)
                .add(EntityAttributes.ARMOR, attributes().wyvernArmor)
                .add(EntityAttributes.ARMOR_TOUGHNESS, attributes().wyvernArmorToughness)
                .add(EntityAttributes.MOVEMENT_SPEED, attributes().wyvernGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.FLYING_SPEED, attributes().wyvernFlyingSpeed * attributes().dragonFlyingSpeedMultiplier)
                .add(EntityAttributes.JUMP_STRENGTH, 0.42 * 1.5)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().wyvernVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().wyvernBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().wyvernRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().wyvernRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().wyvernBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().wyvernBaseSecondaryAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().wyvernRegenerationFromFood);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<WyvernEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::mainController);
        AnimationController<WyvernEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turnController);
        AnimationController<WyvernEntity> attack = new AnimationController<>(this, "attack", 0, this::attackController);
        AnimationController<WyvernEntity> eye = new AnimationController<>(this, "eye", 0, this::eyeController);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "flap" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 3, 0.7F);
                case "woosh" -> playSound(URSounds.DRAGON_WOOSH, 2, 1);
                case "step" -> playSound(URSounds.WYVERN_STEP, 1, 1);
            }
    }

    private <ENTITY extends GeoEntity> void soundListenerAttack(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "shoot" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, 2, 1);
                case "bite" ->  playSound(URSounds.WYVERN_BITE, 1, 1);
            }
    }

    private <A extends GeoEntity> PlayState eyeController(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState mainController(AnimationState<A> event) {
        if (event.getController().hasAnimationFinished()) event.getController().forceAnimationReset();
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isSecondaryAttack()) {
                event.getController().setAnimationSpeed(1/ getCooldownModifier());
                return loopAnim("fly.attack", event);
            }
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (isGliding() || shouldGlide) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f && !isClientSpectator()) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.getController().setAnimationSpeed(Math.max(animationSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.getController().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
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
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + getAttackType(), event);
        if (isPrimaryAttack()) {
            if (isFlying() && (isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.fly.range", event);
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return URSounds.WYVERN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return URSounds.WYVERN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return URSounds.WYVERN_DEATH;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        RegistryEntry<StatusEffect> type = effect.getEffectType();
        return !(type == URStatusEffects.ACID || type == StatusEffects.POISON || type == StatusEffects.HUNGER);
    }

    @Override
    public void tick() {
        super.tick();

        float dHeight;
        float dWidth;
        float dMountedOffset;
        if (isFlying()) {
            dWidth = 2.95f;
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                dHeight = 1f;
                dMountedOffset = 0.75f;
            } else {
                dHeight = 2.95f;
                dMountedOffset = 2.3f;
            }
        } else {
            dHeight = 2.95f;
            dMountedOffset = 2.3f;
            dWidth = 1.8f;
        }
        setHitboxModifiers(dHeight, dWidth, dMountedOffset);

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed && getSecondaryAttackCooldown() == 0) {
                meleeAttack();
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) shoot();
        }

        updateChildParts();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isSaddleItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.WYVERN_SADDLES);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
            player.setStackInHand(hand, consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value()));
            if (random.nextInt(3) == 0) setTamingProgress(getTamingProgress() - 2);
            else setTamingProgress(getTamingProgress() - 1);
            if (player.isCreative()) setTamingProgress(0);
            if (getTamingProgress() <= 0) {
                setOwner(player);
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            } else {
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
            }
            setPersistent();
            return ActionResult.SUCCESS;
        }

        if (isTamed()) {
            if (player.isSneaking() && itemStack.isEmpty() && isOwner(player)) {
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }

            if (itemStack.getItem() == Items.GLASS_BOTTLE && isOwner(player)) {
                Item bottle = itemStack.getItem();
                ItemStack potion = new ItemStack(Items.POTION);
                potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(URPotions.ACID));
                player.incrementStat(Stats.USED.getOrCreateStat(bottle));
                getWorld().playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, player.getSoundCategory(), 1.0F, 1.0F);
                player.setStackInHand(hand, consumeGivenItem(player, itemStack));
                player.giveItemStack(potion);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return super.getBaseDimensions(pose).withEyeHeight(getHeight() * 0.95f);
    }

    @Override
    public double getSwimHeight() {
        return 1;
    }

    public void shoot() {
        if (getWorld().isClient()) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        float yaw = getYawWithAdjustment();
        for (int i = 0; i < 5; ++i) {
            AcidBlastEntity projectileEntity = new AcidBlastEntity(getWorld(), this);
            projectileEntity.setPosition(head.getX(), head.getY(), head.getZ());
            projectileEntity.setVelocity(this, getPitch(), yaw, 0.5f, 3.0f, 5.0f);
            getWorld().spawnEntity(projectileEntity);
        }
    }

    public float getYawProgressLimit() {
        return 90;
    }

    public void meleeAttack() {
        if (!(getWorld() instanceof ServerWorld world)) return;
        List<LivingEntity> list = getWorld().getEntitiesByClass(LivingEntity.class,  getAttackBox(), this::canTarget);
        LivingEntity target = null;
        if (!list.isEmpty()) {
            target = list.getFirst();
            for (LivingEntity entry : list) {
                if (squaredDistanceTo(entry) < squaredDistanceTo(target)) target = entry;
            }
        }
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        if (isFlying()) URPacketHelper.playSound(this, URSounds.WYVERN_BITE, SoundCategory.NEUTRAL, 1, 1, 3);
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(world, target);
        }
    }

    @Override
    public Box getAttackBox() {
        double modifier = isFlying() ? getWidthMod() / 2 : (getWidthMod() + 0.1);
        double x = -Math.sin(Math.toRadians(getYaw())) * modifier;
        double z = Math.cos(Math.toRadians(getYaw())) * modifier;
        double y = isFlying() ? -2 : 0;
        return new Box(getPos().getX() + x - getWidthMod() / 1.5, getPos().getY() + y, getPos().getZ() + z - getWidthMod() / 1.5,
                getPos().getX() + x + getWidthMod() / 1.5, getPos().getY() + getHeight() + 1, getPos().getZ() + z + getWidthMod() / 1.5);
    }

    @Override
    public String getDefaultVariant() {
        return "green";
    }

    @Override
    public float getHeightModTransSpeed() {
        return (float) (0.13 * animationSpeed * getScale());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            return WyvernScreenHandler.createScreenHandler(syncId, inv, inventory);
        }
        return null;
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack){
        return itemStack.isIn(URTags.WYVERN_FOOD);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack){
        return itemStack.isIn(URTags.WYVERN_TAMING_ITEM);
    }

    @Override
    public int getLimitPerChunk() {
        return URConfig.getConfig().wyvernMaxGroupSize * 2;
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    public void updateChildParts() {
        Vec2f wingLeftScale;
        Vec2f wingRightScale;

        Vector3f wingLeftPos;
        Vector3f wingRightPos;
        Vector3f neckPos;
        Vector3f headPos;
        Vector3f tail1Pos;
        Vector3f tail2Pos;
        Vector3f tail3Pos;

        float yawOffset = getNormalizedRotationProgress();
        float pitchOffset = tiltProgress / TRANSITION_TICKS;

        if (isFlying()) {
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                if (getTiltState() == 2) {
                    wingLeftPos = new Vector3f(2, 0, -0.5f);
                    wingLeftScale = new Vec2f(1, 1.5f);

                    wingRightPos = new Vector3f(-2, 0, -0.5f);
                    wingRightScale = new Vec2f(1, 1.5f);
                } else {
                    wingLeftPos = new Vector3f(2.5f, 0, -0.5f);
                    wingLeftScale = new Vec2f(1, 2.5f);

                    wingRightPos = new Vector3f(-2.5f, 0, -0.5f);
                    wingRightScale = new Vec2f(1, 2.5f);
                }
                neckPos = new Vector3f(yawOffset * 0.5f, pitchOffset * 1, 1.75f);
                headPos = new Vector3f(yawOffset * 1.25f, pitchOffset * 1.5f, 2.75f - Math.abs(yawOffset) * 0.5f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 0.25f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 0.625f, -3 + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, -pitchOffset * 1 , -4 + Math.abs(yawOffset) * 1);
            } else {
                wingLeftPos = new Vector3f(3, 0, -0.5f);
                wingLeftScale = new Vec2f(3, 3);

                wingRightPos = new Vector3f(-3, 0, -0.5f);
                wingRightScale = new Vec2f(3, 3);

                neckPos = new Vector3f(0, 3, 1);
                headPos = new Vector3f(yawOffset, 3.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, 1, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, 0.5f, -2.6f + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, -0.2f , -3.2f + Math.abs(yawOffset) * 1);
            }
        } else {
            if (getIsSitting()) {
                wingLeftPos = new Vector3f(1.3333334f, 0, 0);
                wingLeftScale = new Vec2f(1.5f, 2);

                wingRightPos = new Vector3f(-1.3333334f, 0, 0);
                wingRightScale = new Vec2f(1.5f, 2);

                neckPos = new Vector3f(0,  2.75f, 0.5f);
                headPos = new Vector3f(0, 3, 1f);
                tail1Pos = new Vector3f(0, 0.3f, -1.6f);
                tail2Pos = new Vector3f(0, 0.2f, -2.6f);
                tail3Pos = new Vector3f(0, 0.1f , -3.6f);

            } else {
                wingLeftPos = new Vector3f(1, 0.5f, 0);
                wingLeftScale = new Vec2f(2, 1.5f);

                wingRightPos = new Vector3f(-1, 0.5f, 0);
                wingRightScale = new Vec2f(2, 1.5f);

                neckPos = new Vector3f(0, 3, 1);
                headPos = new Vector3f(yawOffset, 3.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.25f, 1.5f, -1.6f);
                tail2Pos = new Vector3f(yawOffset * 0.75f, 1.0f, -2.6f);
                tail3Pos = new Vector3f(yawOffset * 1.45f, 0.25f, -3.2f);
            }
        }

        wingLeft.setRelativePos(wingLeftPos);
        wingLeft.setScale(wingLeftScale);

        wingRight.setRelativePos(wingRightPos);
        wingRight.setScale(wingRightScale);

        head.setRelativePos(headPos);
        head.setScale(1 ,1);

        neck.setRelativePos(neckPos);
        neck.setScale(1 ,1);

        tail1.setRelativePos(tail1Pos);
        tail1.setScale(1 ,1);

        tail2.setRelativePos(tail2Pos);
        tail2.setScale(1 ,1);

        tail3.setRelativePos(tail3Pos);
        tail3.setScale(1 ,1);
    }
}
