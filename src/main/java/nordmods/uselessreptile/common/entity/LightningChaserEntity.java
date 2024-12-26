package nordmods.uselessreptile.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventListener;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserBailOutGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRevengeGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRoamAroundGoal;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import nordmods.uselessreptile.common.gui.LightningChaserScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URGameEvents;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URTags;
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
import java.util.function.BiConsumer;

public class LightningChaserEntity extends URRideableFlyingDragonEntity implements MultipartEntity {
    private int shockwaveDelay = -1;
    private int shootDelay = -1;
    private int bailOutTimer = 6000;
    private boolean shouldBailOut = false;
    private boolean isChallenger = false;
    public BlockPos roamingSpot;
    private static final Identifier THUNDERSTORM_BONUS = UselessReptile.id("thunderstorm_bonus");
    private final URDragonPart wing1Left = new URDragonPart(this);
    private final URDragonPart wing1Right = new URDragonPart(this);
    private final URDragonPart wing2Left = new URDragonPart(this);
    private final URDragonPart wing2Right = new URDragonPart(this);
    private final URDragonPart neck1 = new URDragonPart(this);
    private final URDragonPart neck2 = new URDragonPart(this);
    private final URDragonPart head = new URDragonPart(this);
    private final URDragonPart tail1 = new URDragonPart(this);
    private final URDragonPart tail2 = new URDragonPart(this);
    private final URDragonPart tail3 = new URDragonPart(this);
    private final URDragonPart[] parts = new URDragonPart[]{wing1Left, wing2Left, wing1Right, wing2Right, neck1, neck2, head, tail1, tail2, tail3};
    protected final EntityGameEventHandler<LightningStrikeEventListener> lightningStrikeEventHandler = new EntityGameEventHandler<>(new LightningStrikeEventListener
            (new EntityPositionSource(this, getStandingEyeHeight()), URGameEvents.LIGHTNING_STRIKE_FAR.value().notificationRadius()));

    public static float BASE_GROUND_SPEED = 0.25f;

    public LightningChaserEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;
        baseTamingProgress = 3;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        ticksUntilHeal = 500;
        specialAttackDuration = 30;
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.add(3, new SitGoal(this));
        goalSelector.add(4, new DragonConsumeFoodFromInventoryGoal(this));
        goalSelector.add(5, new LightningChaserAttackGoal(this));
        goalSelector.add(6, new LightningChaserRoamAroundGoal(this));
        goalSelector.add(6, new LightningChaserBailOutGoal(this));
        goalSelector.add(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.add(8, new DragonReturnToHomePoint(this));
        goalSelector.add(9, new DragonWanderAroundGoal(this));
        goalSelector.add(10, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.add(11, new DragonLookAroundGoal(this));
        targetSelector.add(1, new LightningChaserRevengeGoal(this));
        targetSelector.add(2, new DragonAttackWithOwnerGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.add(2, new UntamedActiveTargetGoal<>(this, PlayerEntity.class, true, null));

    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        if (spawnReason == SpawnReason.EVENT) isChallenger = true;
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getChunk(pos).getInhabitedTime() > 12000) return false;
        return URDragonEntity.canDragonSpawn(type, world, spawnReason, pos, random);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            return LightningChaserScreenHandler.createScreenHandler(syncId, inv, inventory);
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<LightningChaserEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::mainController);
        AnimationController<LightningChaserEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turnController);
        AnimationController<LightningChaserEntity> attack = new AnimationController<>(this, "attack", 0, this::attackController);
        AnimationController<LightningChaserEntity> eye = new AnimationController<>(this, "eye", 0, this::eyeController);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "flap" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 3, 0.6F);
                case "woosh" -> playSound(URSounds.DRAGON_WOOSH, 2, 1);
                case "step" -> playSound(URSounds.LIGHTNING_CHASER_STEP, 1, 1);
                case "flap_heavy" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 3, 0.5F);
            }
    }

    private <ENTITY extends GeoEntity> void soundListenerAttack(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "bite" -> playSound(URSounds.LIGHTNING_CHASER_BITE, 1, 1);
            }
    }

    private <A extends GeoEntity> PlayState eyeController(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState mainController(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animationSpeed);
        event.getController().transitionLength(TRANSITION_TICKS);
        if (isFlying()) {
            if (isSpecialAttack()) {
                event.getController().transitionLength(TRANSITION_TICKS/2);
                event.getController().setAnimationSpeed(getCooldownModifier());
                return loopAnim("fly.shockwave", event);
            }
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (shouldGlide) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f && !isClientSpectator()) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.getController().setAnimationSpeed(Math.max(animationSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (hasSurrendered()) return loopAnim("surrender", event);
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.getController().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turnController(AnimationState<A> event) {
        byte turnState = getTurningState();
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if ((isMoving() || event.isMoving()) && !isMovingBackwards()) {
                if (turnState == 1) return loopAnim("turn.fly.left", event);
                if (turnState == 2) return loopAnim("turn.fly.right", event);
            }
            if (turnState == 1) return loopAnim("turn.fly.idle.left", event);
            if (turnState == 2) return loopAnim("turn.fly.idle.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attackController(AnimationState<A> event) {
        event.getController().setAnimationSpeed(1/ getCooldownModifier());
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + getAttackType(), event);
        if (isPrimaryAttack()) {
            if (isFlying()) {
                if (isSpecialAttack()) return playAnim("attack.range.fly.shockwave", event);
                if ((isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.range.fly", event);
                return playAnim("attack.range.fly.idle", event);
            }
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    public static DefaultAttributeContainer.Builder createLightningChaserAttributes() {
        return createDragonAttributes()
                .add(EntityAttributes.ATTACK_DAMAGE, attributes().lightningChaserDamage)
                .add(EntityAttributes.ATTACK_KNOCKBACK, attributes().lightningChaserKnockback)
                .add(EntityAttributes.MAX_HEALTH, attributes().lightningChaserHealth)
                .add(EntityAttributes.ARMOR, attributes().lightningChaserArmor)
                .add(EntityAttributes.ARMOR_TOUGHNESS, attributes().lightningChaserArmorToughness)
                .add(EntityAttributes.MOVEMENT_SPEED, attributes().lightningChaserGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.FLYING_SPEED, attributes().lightningChaserFlyingSpeed * attributes().dragonFlyingSpeedMultiplier)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().lightningChaserVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().lightningChaserBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().lightningChaserRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().lightningChaserRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().lightningChaserBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().lightningChaserBaseSecondaryAttackCooldown)
                .add(URAttributes.DRAGON_SPECIAL_ATTACK_COOLDOWN, attributes().lightningChaserBaseSpecialAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().lightningChaserRegenerationFromFood);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SURRENDERED, false);
    }
    public static final TrackedData<Boolean> SURRENDERED = DataTracker.registerData(LightningChaserEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean hasSurrendered() {return dataTracker.get(SURRENDERED);}
    public void setSurrendered(boolean state) {dataTracker.set(SURRENDERED, state);}

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        if (!isTamed()) {
            tag.putInt("BailOutTimer", bailOutTimer);
            tag.putBoolean("HasSurrendered", hasSurrendered());
            tag.putBoolean("BailingOut", shouldBailOut);
            tag.putBoolean("IsChallenger", isChallenger);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        dataTracker.set(VARIANT, tag.getString("Variant"));

        if (!isTamed()) {
            bailOutTimer = tag.getInt("BailOutTimer");
            setSurrendered(tag.getBoolean("HasSurrendered"));
            shouldBailOut = tag.getBoolean("BailingOut");
            isChallenger = tag.getBoolean("IsChallenger");
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (!isTamed() && isFlying() && getWorld().isThundering() && !getShouldBailOut() && !hasSurrendered()) return URSounds.LIGHTNING_CHASER_DISTANT_ROAR;
        return URSounds.LIGHTNING_CHASER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return URSounds.LIGHTNING_CHASER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return URSounds.LIGHTNING_CHASER_DEATH;
    }

    @Override
    public boolean isInvulnerableTo(ServerWorld world, DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.LIGHTNING_BOLT)) return true;
        else return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    public void tick() {
        super.tick();

        float dHeight;
        float dWidth;
        float dMountedOffset;
        dWidth = 2.95f;
        if (isFlying()) {
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
        }
        setHitboxModifiers(dHeight, dWidth, dMountedOffset);

        if (shockwaveDelay == 0) shockwave();
        if (shockwaveDelay > -1) shockwaveDelay--;

        if (shootDelay == 0) shoot();
        if (shootDelay > -1) shootDelay--;

        if (canBeControlledByRider()) {
            if (isFlying()) {
                if (isSecondaryAttackPressed && getSpecialAttackCooldown() == 0) triggerShockwave();
            }
            else if (isSecondaryAttackPressed && getSecondaryAttackCooldown() == 0) {
                meleeAttack();
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) triggerShoot();
        }

        updateThunderstormBonus();

        if (!shouldBailOut) {
            if (isChallenger) {
                if (getTarget() == null && !isTamed()) {
                    if (bailOutTimer > 0) bailOutTimer--;
                    else {
                        setSurrendered(false);
                        shouldBailOut = true;
                    }
                }
            } else if (getHealth() / getMaxHealth() > 0.5) setSurrendered(false);
            if (hasSurrendered()) {
                if (age % 200 == 0) heal(2);
                setIsSitting(true);
                removeAllPassengers();
            }
        }

        updateChildParts();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isSaddleItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.LIGHTNING_CHASER_SADDLES);
    }

    private void updateThunderstormBonus() {
        if (getWorld().isClient()) return;
        if (getWorld().getLevelProperties().isThundering()) {
            tryAddModifier(EntityAttributes.ARMOR, 4, EntityAttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(EntityAttributes.FLYING_SPEED, 0.2, EntityAttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(EntityAttributes.MOVEMENT_SPEED, 0.05, EntityAttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(EntityAttributes.ATTACK_DAMAGE, 2f, EntityAttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(URAttributes.DRAGON_ACCELERATION_DURATION, -0.33, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_VERTICAL_SPEED, 0.1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            removeModifier(EntityAttributes.ARMOR);
            removeModifier(EntityAttributes.FLYING_SPEED);
            removeModifier(EntityAttributes.MOVEMENT_SPEED);
            removeModifier(EntityAttributes.ATTACK_DAMAGE);
            removeModifier(URAttributes.DRAGON_ACCELERATION_DURATION);
            removeModifier(URAttributes.DRAGON_VERTICAL_SPEED);
            removeModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED);
            removeModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED);
        }
    }

    private void tryAddModifier(RegistryEntry<EntityAttribute> entityAttribute, double bonus, EntityAttributeModifier.Operation operation) {
        if (!getAttributeInstance(entityAttribute).hasModifier(THUNDERSTORM_BONUS))
            getAttributeInstance(entityAttribute)
                    .addTemporaryModifier(new EntityAttributeModifier(THUNDERSTORM_BONUS, bonus, operation));
    }

    private void removeModifier(RegistryEntry<EntityAttribute> entityAttribute) {
        getAttributeInstance(entityAttribute).removeModifier(THUNDERSTORM_BONUS);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource damageSource, float amount) {
        boolean toReturn = super.damage(world, damageSource, amount);
        if (getHealth() / getMaxHealth() < 0.3 && !hasSurrendered() && (getTamingProgress() <= 0 || isTamed())) {
            if (!isDead()) setHealth(getMaxHealth() * 0.3f);
            setInAirTimer(getMaxInAirTimer());
            setTarget(null);
            setSurrendered(true);
            URPacketHelper.playSound(this, URSounds.LIGHTNING_CHASER_SURRENDER, getSoundCategory(), 1, 1,1);
            if (isChallenger) bailOutTimer = 6000;
        }
        return toReturn;
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        if (lightning.getChanneler() != null && getTamingProgress() > 0) setTamingProgress(getTamingProgress() - 1);
        addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400, 3));
        addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400, 1));
        addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 1));
        addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 400, 0));
        lightning.discard();
    }

    public void triggerShoot() {
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        shootDelay = 7;
    }

    public void shoot() {
        float yaw = getYawWithAdjustment();
        LightningBreathEntity.createBeam(this, getPitch(), yaw, head.getPos().add(0,  isFlying() ? -0.6 : -1.25, 0));
    }

    public float getYawProgressLimit() {
        return 45;
    }

    private void shockwave() {
        ShockwaveSphereEntity shockwaveSphereEntity = new ShockwaveSphereEntity(getWorld());
        shockwaveSphereEntity.setOwner(this);
        shockwaveSphereEntity.setPosition(getPos().add(0, getHeightMod(), 0));
        shockwaveSphereEntity.setVelocity(Vec3d.ZERO);
        shockwaveSphereEntity.setNoGravity(true);
        getWorld().spawnEntity(shockwaveSphereEntity);
    }

    public void triggerShockwave() {
        setSpecialAttackCooldown(getMaxSpecialAttackCooldown());
        shockwaveDelay = TRANSITION_TICKS/2;
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
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(world, target);
        }
    }

    @Override
    public Box getAttackBox() {
        Vec3d rotationVec = getRotationVector(0, getYaw()).multiply(2.5);
        return getBoundingBox().offset(rotationVec);
    }

    @Override
    public boolean isSecondaryAttack() {return isFlying() ? getSecondaryAttackCooldown() > getMaxSecondaryAttackCooldown() - 24 : super.isSecondaryAttack();}

    @Override
    protected int getTicksUntilHeal() {
        return getWorld().isThundering() ? (int) (super.getTicksUntilHeal() * 0.5) : super.getTicksUntilHeal();
    }

    @Override
    public String getDefaultVariant() {
        return "grey";
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!isTamed()) {
            if (hasSurrendered() && getTamingProgress() <= 0 || player.isCreative() && isFavoriteFood(itemStack)) {
                setOwner(player);
                setPersistent();
                setSurrendered(false);
                shouldBailOut = false;
                isChallenger = false;
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                return ActionResult.SUCCESS;
            } else if (hasSurrendered() && getTamingProgress() > 0) {
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
                return ActionResult.SUCCESS;
            }
        }

        if (isTamed()) {
            if (player.isSneaking() && itemStack.isEmpty() && isOwner(player)) {
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (hasSurrendered()) return false;
        return super.startRiding(entity, force);
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack){
        return itemStack.isIn(URTags.LIGHTNING_CHASER_FOOD);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack){
        return false;
    }

    public boolean getShouldBailOut() {
        return shouldBailOut;
    }

    public boolean isChallenger() {
        return isChallenger;
    }

    @Override
    public int getLimitPerChunk() {
        return URConfig.getConfig().lightningChaserMaxGroupSize * 2;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (hasSurrendered() || getShouldBailOut()) return false;
        return super.canTarget(target);
    }

    protected class LightningStrikeEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public LightningStrikeEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public PositionSource getPositionSource() {return this.positionSource;}

        public int getRange() {return this.range;}

        @Override
        public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
            if (event != URGameEvents.LIGHTNING_STRIKE_FAR) return false;
            if (isTamed() || getTarget() != null) return false;
            if (emitter.sourceEntity() instanceof LightningEntity lightning) {
                PlayerEntity target = lightning.getChanneler();
                if (target != null) {
                    if (!canTarget(target)) return false;
                    setTarget(target);
                    URPacketHelper.playSound(LightningChaserEntity.this, URSounds.LIGHTNING_CHASER_ACCEPT_CHALLENGE, getSoundCategory(), 1, 1,1);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
        if (getWorld() instanceof ServerWorld serverWorld) callback.accept(lightningStrikeEventHandler, serverWorld);
        super.updateEventHandler(callback);
    }

    @Override
    public boolean disablesShield() {
        return isPrimaryAttack();
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(getWorld() instanceof ServerWorld world)) return false;
        boolean shouldBreakBlocks = isTamed() ? URConfig.getConfig().lightningChaserGriefing.canTamedBreak() : URConfig.getConfig().lightningChaserGriefing.canUntamedBreak();
        return shouldBreakBlocks && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    public void updateChildParts() {
        Vec2f wing1LeftScale;
        Vec2f wing1RightScale;
        Vec2f wing2LeftScale;
        Vec2f wing2RightScale;
        
        Vector3f wing1LeftPos;
        Vector3f wing1RightPos;
        Vector3f wing2LeftPos;
        Vector3f wing2RightPos;
        Vector3f neck1Pos;
        Vector3f neck2Pos;
        Vector3f headPos;
        Vector3f tail1Pos;
        Vector3f tail2Pos;
        Vector3f tail3Pos;

        float yawOffset = getNormalizedRotationProgress();
        float pitchOffset = tiltProgress / TRANSITION_TICKS;

        if (isFlying()) {
            if (isMoving() && !isMovingBackwards() && !isSpecialAttack()) {
                if (getTiltState() == 2) {
                    wing1LeftPos = new Vector3f(2, 0, 0.5f);
                    wing1LeftScale = new Vec2f(1, 1.5f);

                    wing2LeftPos = new Vector3f(2, 0, -0.5f);
                    wing2LeftScale = new Vec2f(1, 1.5f);

                    wing1RightPos = new Vector3f(-2, 0, 0.5f);
                    wing1RightScale = new Vec2f(1, 1.5f);

                    wing2RightPos = new Vector3f(-2, 0, -0.5f);
                    wing2RightScale = new Vec2f(1, 1.5f);
                } else {
                    wing1LeftPos = new Vector3f(2.5f, 0, 0);
                    wing1LeftScale = new Vec2f(1, 2.5f);

                    wing2LeftPos = new Vector3f(5, 0, 0);
                    wing2LeftScale = new Vec2f(1, 2.5f);

                    wing1RightPos = new Vector3f(-2.5f, 0, 0);
                    wing1RightScale = new Vec2f(1, 2.5f);

                    wing2RightPos = new Vector3f(-5, 0, 0);
                    wing2RightScale = new Vec2f(1, 2.5f);
                }
                neck1Pos = new Vector3f(yawOffset * 0.25f, pitchOffset * 0.75f, 2f);
                neck2Pos = new Vector3f(yawOffset * 0.75f, pitchOffset * 1, 2.75f - Math.abs(yawOffset) * 0.25f);
                headPos = new Vector3f(yawOffset * 1.5f, pitchOffset * 1.25f, 3.5f - Math.abs(yawOffset) * 0.5f);

                tail1Pos = new Vector3f(yawOffset * 0.25f, -pitchOffset * 1, -2);
                tail2Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 1.25f, -3);
                tail3Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 1.5f , -4 + Math.abs(yawOffset) * 0.25f);
            } else {
                wing1LeftPos = new Vector3f(3, 0.75f, -0.5f);
                wing1LeftScale = new Vec2f(1.5f, 3);

                wing2LeftPos = new Vector3f(3.5f, 0.75f, -1);
                wing2LeftScale = new Vec2f(1.5f, 2);

                wing1RightPos = new Vector3f(-3, 0.75f, -0.5f);
                wing1RightScale = new Vec2f(1.5f, 3);

                wing2RightPos = new Vector3f(-3.5f, 0.75f, -1);
                wing2RightScale = new Vec2f(1.5f, 2);

                neck1Pos = new Vector3f(0, 3, 1);
                neck2Pos = new Vector3f(yawOffset * 0.5f, 3, 1.5f);
                headPos = new Vector3f(yawOffset,  3.1f, 2f);

                tail1Pos = new Vector3f(yawOffset * 0.5f, -0.5f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, -1.5f, -2.25f);
                tail3Pos = new Vector3f(yawOffset * 2f, -2.5f , -2.5f);
            }
        } else {
            if (getIsSitting()) {
                wing1LeftPos = new Vector3f(1.5f, 0, 0.5f);
                wing1LeftScale = new Vec2f(2, 1.5f);

                wing2LeftPos = new Vector3f(1.75f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2f(1.5f, 1.5f);

                wing1RightPos = new Vector3f(-1.5f, 0, 0.5f);
                wing1RightScale = new Vec2f(2, 1.5f);

                wing2RightPos = new Vector3f(-1.75f, 0.75f, -0.5f);
                wing2RightScale = new Vec2f(1.5f, 1.5f);

                if (hasSurrendered()) {
                    neck1Pos = new Vector3f(0, 1.6f, 1);
                    neck2Pos = new Vector3f(yawOffset * 0.4f, 1.3f, 1.7f);
                    headPos = new Vector3f(yawOffset * 0.8f,  0.5f, 2.4f);
                } else {
                    neck1Pos = new Vector3f(0, 2.5f, 1);
                    neck2Pos = new Vector3f(yawOffset * 0.4f, 2.8f, 1.5f);
                    headPos = new Vector3f(yawOffset * 0.8f, 3.1f, 2f);
                }

                tail1Pos = new Vector3f(0, 0.3f, -2.2f);
                tail2Pos = new Vector3f(0, 0.35f, -3.2f);
                tail3Pos = new Vector3f(0, 0.4f , -4.2f);

            } else {
                wing1LeftPos = new Vector3f(1.5f, 0, 0.5f);
                wing1LeftScale = new Vec2f(2, 1.5f);

                wing2LeftPos = new Vector3f(1.75f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2f(1.5f, 1.5f);

                wing1RightPos = new Vector3f(-1.5f, 0, 0.5f);
                wing1RightScale = new Vec2f(2, 1.5f);

                wing2RightPos = new Vector3f(-1.75f, 0.75f, -0.5f);
                wing2RightScale = new Vec2f(1.5f, 1.5f);

                neck1Pos = new Vector3f(0, 2, 1);
                neck2Pos = new Vector3f(yawOffset * 0.4f, 2.25f, 1.5f);
                headPos = new Vector3f(yawOffset * 0.8f, 2.6f, 2f);

                tail1Pos = new Vector3f(yawOffset * 0.2f, 1.5f, -2.1f);
                tail2Pos = new Vector3f(yawOffset * 0.4f, 2.2f, -2.8f);
                tail3Pos = new Vector3f(yawOffset * 0.8f, 2.5f, -3.7f);
            }
        }

        wing1Left.setRelativePos(wing1LeftPos);
        wing1Left.setScale(wing1LeftScale);

        wing2Left.setRelativePos(wing2LeftPos);
        wing2Left.setScale(wing2LeftScale);

        wing1Right.setRelativePos(wing1RightPos);
        wing1Right.setScale(wing1RightScale);

        wing2Right.setRelativePos(wing2RightPos);
        wing2Right.setScale(wing2RightScale);

        head.setRelativePos(headPos);
        head.setScale(1, 1);

        neck1.setRelativePos(neck1Pos);
        neck1.setScale(1, 1);

        neck2.setRelativePos(neck2Pos);
        neck2.setScale(1, 1);

        tail1.setRelativePos(tail1Pos);
        tail1.setScale(1, 1);

        tail2.setRelativePos(tail2Pos);
        tail2.setScale(1, 1);

        tail3.setRelativePos(tail3Pos);
        tail3.setScale(1, 1);
    }
}
