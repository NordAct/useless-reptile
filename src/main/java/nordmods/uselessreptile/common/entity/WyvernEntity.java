package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.wyvern.WyvernAttackGoal;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;
import nordmods.uselessreptile.common.init.URPotions;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.URPacketHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

public class WyvernEntity extends URRideableFlyingDragonEntity implements MultipartEntity {

    private final URDragonPart wingLeft = new URDragonPart(this);
    private final URDragonPart wingRight = new URDragonPart(this);
    private final URDragonPart neck = new URDragonPart(this);
    private final URDragonPart head = new URDragonPart(this);
    private final URDragonPart tail1 = new URDragonPart(this);
    private final URDragonPart tail2 = new URDragonPart(this);
    private final URDragonPart tail3 = new URDragonPart(this);
    private final URDragonPart[] parts = new URDragonPart[]{wingLeft, wingRight, neck, head, tail1, tail2, tail3};

    public WyvernEntity(EntityType<? extends URRideableFlyingDragonEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        baseSecondaryAttackCooldown = attributes().wyvernBaseSecondaryAttackCooldown;
        basePrimaryAttackCooldown = attributes().wyvernBasePrimaryAttackCooldown;
        baseAccelerationDuration = attributes().wyvernBaseAccelerationDuration;
        baseTamingProgress = 128;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        rotationSpeedGround = attributes().wyvernRotationSpeedGround;
        rotationSpeedAir = attributes().wyvernRotationSpeedAir;
        verticalSpeed = attributes().wyvernVerticalSpeed;
        regenerationFromFood = attributes().wyvernRegenerationFromFood;
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
        goalSelector.add(8, new DragonWanderAroundGoal(this));
        goalSelector.add(8, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.add(9, new DragonLookAroundGoal(this));
        targetSelector.add(5, new UntamedActiveTargetGoal<>(this, ChickenEntity.class, true, null));
        targetSelector.add(5, new DragonAttackWithOwnerGoal<>(this));
        targetSelector.add(4, new DragonRevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createWyvernAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attributes().wyvernDamage * attributes().dragonDamageMultiplier)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, attributes().wyvernKnockback * URMobAttributesConfig.getConfig().dragonKnockbackMultiplier)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, attributes().wyvernHealth * attributes().dragonHealthMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR, attributes().wyvernArmor * attributes().dragonArmorMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, attributes().wyvernArmorToughness * attributes().dragonArmorToughnessMultiplier)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, attributes().wyvernGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, attributes().wyvernFlyingSpeed * attributes().dragonFlyingSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<WyvernEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::main);
        AnimationController<WyvernEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turn);
        AnimationController<WyvernEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<WyvernEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
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

    private <A extends GeoEntity> PlayState eye(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState main(AnimationState<A> event) {
        //MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal(event.getController().getAnimationState().toString()), false);
        if (event.getController().hasAnimationFinished()) event.getController().forceAnimationReset();
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isSecondaryAttack()) {
                event.getController().setAnimationSpeed(1/calcCooldownMod());
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
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + attackType, event);
        if (isPrimaryAttack()) {
            if (isFlying() && (isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.fly.range", event);
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockPos).isIn(URTags.WYVERN_SPAWNABLE_ON);
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
        StatusEffect type = effect.getEffectType();
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
                LivingEntity target = getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, getX(), getY(), getZ(), getAttackBox());
                meleeAttack(target);
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) shoot();
        }

        updateChildParts();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
            eat(player, hand, itemStack);
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
            if (player.isSneaking() && itemStack.isEmpty() && isOwnerOrCreative(player)) {
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }

            if (itemStack.getItem() == Items.GLASS_BOTTLE && isOwnerOrCreative(player)) {
                Item bottle = itemStack.getItem();
                ItemStack potion = PotionUtil.setPotion(new ItemStack(Items.POTION), URPotions.ACID);
                player.incrementStat(Stats.USED.getOrCreateStat(bottle));
                getWorld().playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                if (itemStack.getCount() > 1) ItemUsage.exchangeStack(itemStack, player, potion);
                else player.setStackInHand(hand, potion);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.95f;
    }

    @Override
    public double getSwimHeight() {
        return 1;
    }

    public void shoot() {
        if (getWorld().isClient()) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        float progress = rotationProgress / TRANSITION_TICKS;
        float yaw = 0;
        boolean check = isFlying() && isMoving() && !isMovingBackwards();
        if (canBeControlledByRider()) {
            yaw = check ? 90 : 45;
            yaw *= progress;
        }
        for (int i = 0; i < 5; ++i) {
            AcidBlastEntity projectileEntity = new AcidBlastEntity(getWorld(), this);
            projectileEntity.setPosition(head.getX(), head.getY(), head.getZ());
            projectileEntity.setVelocity(this, getPitch(), getYaw() - yaw, 0.5f, 3.0f, 5.0f);
            getWorld().spawnEntity(projectileEntity);
        }
    }

    public void meleeAttack(LivingEntity target) {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        attackType = random.nextInt(3)+1;
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        if (isFlying()) URPacketHelper.playSound(this, URSounds.WYVERN_BITE, SoundCategory.NEUTRAL, 1, 1, 3);
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(target);
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
    public float getJumpBoostVelocityModifier() {
        float jumpBoost = hasStatusEffect(StatusEffects.JUMP_BOOST) ? (0.1F * (float) (getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1)) : 0.0f;
        return 0.2f + jumpBoost;
    }

    @Override
    protected float getJumpVelocity() {
        if (hasControllingPassenger()) super.getJumpVelocity();
        return 0.42F * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier()/2;
    }

    @Override
    public float getHeightModTransSpeed() {
        return (float) (0.13 * animationSpeed);
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
    protected void updateEquipment() {
        updateSaddle();
        updateBanner();
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack){
        return itemStack.isOf(Items.CHICKEN);
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

        float yawOffset = rotationProgress / TRANSITION_TICKS;
        float pitchOffset = tiltProgress / TRANSITION_TICKS;

        if (isFlying()) {
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                if (getTiltState() == 2) {
                    wingLeftPos = new Vector3f(wingLeft.getWidth() + 0.5f, 0, -0.5f);
                    wingLeftScale = new Vec2f(1, 1.5f);

                    wingRightPos = new Vector3f(-wingRight.getWidth() - 0.5f, 0, -0.5f);
                    wingRightScale = new Vec2f(1, 1.5f);
                } else {
                    wingLeftPos = new Vector3f(wingLeft.getWidth(), 0, -0.5f);
                    wingLeftScale = new Vec2f(1, 2.5f);

                    wingRightPos = new Vector3f(-wingRight.getWidth(), 0, -0.5f);
                    wingRightScale = new Vec2f(1, 2.5f);
                }
                neckPos = new Vector3f(yawOffset * 0.5f, pitchOffset * 1, 1.75f);
                headPos = new Vector3f(yawOffset * 1.25f, pitchOffset * 1.5f, 2.75f - Math.abs(yawOffset) * 0.5f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 0.25f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 0.625f, -3 + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, -pitchOffset * 1 , -4 + Math.abs(yawOffset) * 1);
            } else {
                wingLeftPos = new Vector3f(wingLeft.getWidth(), 0, -0.5f);
                wingLeftScale = new Vec2f(getHeight(), 3);

                wingRightPos = new Vector3f(-wingRight.getWidth(), 0, -0.5f);
                wingRightScale = new Vec2f(getHeight(), 3);

                neckPos = new Vector3f(0, getHeight(), 1);
                headPos = new Vector3f(yawOffset,  getHeight() + 0.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, getHeight() - 2, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, getHeight() - 2.5f, -2.6f + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, getHeight() - 3.2f , -3.2f + Math.abs(yawOffset) * 1);
            }
        } else {
            if (getIsSitting()) {
                wingLeftPos = new Vector3f(getWidth() / 1.5f, 0, 0);
                wingLeftScale = new Vec2f(1.5f, getWidth());

                wingRightPos = new Vector3f(-getWidth() / 1.5f, 0, 0);
                wingRightScale = new Vec2f(1.5f, getWidth());

                neckPos = new Vector3f(0, getHeight(), 1f);
                headPos = new Vector3f(0,  getHeight() - 0.25f, 0.5f);
                tail1Pos = new Vector3f(0, 0.3f, -getWidth() + 0.4f);
                tail2Pos = new Vector3f(0, 0.2f, -getWidth() - 0.6f);
                tail3Pos = new Vector3f(0, 0.1f , -getWidth() - 1.6f);

            } else {
                wingLeftPos = new Vector3f(getWidth() / 2, 0.5f, 0);
                wingLeftScale = new Vec2f(getHeight() - 1, getWidth() * 0.75f);

                wingRightPos = new Vector3f(-getWidth() / 2, 0.5f, 0);
                wingRightScale = new Vec2f(getHeight() - 1, getWidth() * 0.75f);

                neckPos = new Vector3f(0, getHeight(), 1);
                headPos = new Vector3f(yawOffset,  getHeight() + 0.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.25f, getHeight() - 1.5f, -getWidth() + 0.4f);
                tail2Pos = new Vector3f(yawOffset * 0.75f, getHeight() - 2f,  -getWidth() - 0.6f);
                tail3Pos = new Vector3f(yawOffset * 1.45f, getHeight() - 2.75f , -getWidth() - 1.2f);
            }
        }

        wingLeft.setRelativePos(wingLeftPos);
        wingLeft.setScale(wingLeftScale);

        wingRight.setRelativePos(wingRightPos);
        wingRight.setScale(wingRightScale);

        head.setRelativePos(headPos);
        neck.setRelativePos(neckPos);
        tail1.setRelativePos(tail1Pos);
        tail2.setRelativePos(tail2Pos);
        tail3.setRelativePos(tail3Pos);
    }
}
