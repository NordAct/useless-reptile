package nordmods.uselessreptile.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserBailOutGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRevengeGoal;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRoamAroundGoal;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.URPacketHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;
import java.util.function.BiConsumer;

public class LightningChaserEntity extends URRideableFlyingDragonEntity implements MultipartEntity, ShooterDragon {
    private int shockwaveDelay = -1;
    private int shootDelay = -1;
    private int bailOutTimer = 6000;
    private boolean shouldBailOut = false;
    private boolean isChallenger = false;
    private static final ResourceLocation THUNDERSTORM_BONUS = UselessReptile.id("thunderstorm_bonus");
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
    protected final DynamicGameEventListener<LightningStrikeEventListener> lightningStrikeEventHandler = new DynamicGameEventListener<>(new LightningStrikeEventListener
            (new EntityPositionSource(this, getEyeHeight()), URGameEvents.LIGHTNING_STRIKE_FAR.value().notificationRadius()));
    private ShootingPoint shootingPoint = new ShootingPoint(position(), getLookAngle());
    public static final float BASE_GROUND_SPEED = 0.25f;

    public LightningChaserEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        ticksUntilHeal = 500;
        specialAttackDuration = 27;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal(this));
        goalSelector.addGoal(5, new LightningChaserAttackGoal(this));
        goalSelector.addGoal(6, new LightningChaserRoamAroundGoal(this));
        goalSelector.addGoal(6, new LightningChaserBailOutGoal(this));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 60));
        goalSelector.addGoal(8, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(10, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(11, new DragonLookAroundGoal(this));
        targetSelector.addGoal(1, new LightningChaserRevengeGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, Player.class, true, null));

    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason == EntitySpawnReason.EVENT) isChallenger = true;
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    public static boolean canDragonSpawn(EntityType<? extends Mob> type, LevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        if (world.getChunk(pos).getInhabitedTime() > 12000) return false;
        return URDragonEntity.canDragonSpawn(type, world, spawnReason, pos, random);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (!level().isClientSide()) GUIEntityToRenderS2CPacket.send((ServerPlayer) player, this);
        return new URDragonScreenHandler(URScreenHandlers.LIGHTNING_CHASER_INVENTORY, syncId, inv, getInventory());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<LightningChaserEntity> main = new AnimationController<>("main", TRANSITION_TICKS, this::mainController);
        AnimationController<LightningChaserEntity> turn = new AnimationController<>("turn", TRANSITION_TICKS, this::turnController);
        AnimationController<LightningChaserEntity> attack = new AnimationController<>("attack", 0, this::attackController);
        AnimationController<LightningChaserEntity> eye = new AnimationController<>("eye", 0, this::eyeController);
        main.setSoundKeyframeHandler(this::soundHandler);
        attack.setSoundKeyframeHandler(this::soundHandler);
        turn.setSoundKeyframeHandler(this::soundHandler);
        eye.setSoundKeyframeHandler(this::soundHandler);
        animationData.add(main, turn, attack, eye);
    }

    private <A extends GeoEntity> PlayState eyeController(AnimationTest<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState mainController(AnimationTest<A> event) {
        event.controller().transitionLength((int) (TRANSITION_TICKS / event.controller().getAnimationSpeed()));
        event.controller().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isSpecialAttack()) {
                event.controller().setAnimationSpeed(1);
                event.controller().transitionLength(TRANSITION_TICKS/2);
                event.controller().setAnimationSpeed(getCooldownModifier());
                return loopAnim("fly.shockwave", event);
            }
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (isFlyGliding()) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.controller().setAnimationSpeed(Math.max(animationSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (hasSurrendered()) return loopAnim("surrender", event);
        if (isOrderedToSit() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.controller().setAnimationSpeed(1);
        if (isDancing() && !isVehicle()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turnController(AnimationTest<A> event) {
        byte turnState = getTurningState();
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

    private <A extends GeoEntity> PlayState attackController(AnimationTest<A> event) {
        event.controller().setAnimationSpeed(1/ getCooldownModifier());
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

    public static AttributeSupplier.Builder createLightningChaserAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().lightningChaserDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().lightningChaserKnockback)
                .add(Attributes.MAX_HEALTH, attributes().lightningChaserHealth)
                .add(Attributes.ARMOR, attributes().lightningChaserArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().lightningChaserArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().lightningChaserGroundSpeed)
                .add(Attributes.FLYING_SPEED, attributes().lightningChaserFlyingSpeed)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().lightningChaserVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().lightningChaserBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().lightningChaserRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().lightningChaserRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().lightningChaserBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().lightningChaserBaseSecondaryAttackCooldown)
                .add(URAttributes.DRAGON_SPECIAL_ATTACK_COOLDOWN, attributes().lightningChaserBaseSpecialAttackCooldown);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SURRENDERED, false);
    }
    public static final EntityDataAccessor<Boolean> SURRENDERED = SynchedEntityData.defineId(LightningChaserEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean hasSurrendered() {return entityData.get(SURRENDERED);}
    public void setSurrendered(boolean state) {entityData.set(SURRENDERED, state);}

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        if (!isTame()) {
            tag.putInt("BailOutTimer", bailOutTimer);
            tag.putBoolean("HasSurrendered", hasSurrendered());
            tag.putBoolean("BailingOut", shouldBailOut);
            tag.putBoolean("IsChallenger", isChallenger);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        if (!isTame()) {
            bailOutTimer = tag.getIntOr("BailOutTimer", bailOutTimer);
            setSurrendered(tag.getBooleanOr("HasSurrendered", false));
            shouldBailOut = tag.getBooleanOr("BailingOut", false);
            isChallenger = tag.getBooleanOr("IsChallenger", false);
        }
    }

    public void playAmbientSound() {
        boolean playRoar = !isTame() && isFlying() && level().isThundering() && !getShouldBailOut() && !hasSurrendered();
        SoundInfo soundInfo = getSoundInfo(playRoar ? "roar" : "idle");
        if (soundInfo != null) playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel world, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.LIGHTNING_BOLT)) return true;
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
                if (isSecondaryAttackPressed() && getSpecialAttackCooldown() == 0) triggerShockwave();
            }
            else if (isSecondaryAttackPressed() && getSecondaryAttackCooldown() == 0) {
                meleeAttack();
            }
            if (isPrimaryAttackPressed() && getPrimaryAttackCooldown() == 0) triggerShoot();
        }

        updateThunderstormBonus();

        if (!level().isClientSide() && !shouldBailOut) {
            if (isChallenger) {
                if (getTarget() == null && !isTame()) {
                    if (bailOutTimer > 0) bailOutTimer--;
                    else {
                        setSurrendered(false);
                        shouldBailOut = true;
                    }
                }
            } else if (getHealth() / getMaxHealth() > 0.5) setSurrendered(false);
            if (hasSurrendered()) {
                if (tickCount % 200 == 0) heal(2);
                setOrderedToSit(true);
                ejectPassengers();
            }
        }

        getLookControl().setLockRotation(hasSurrendered() && !isFlying());

        updateChildParts();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isSaddle(ItemStack itemStack) {
        return itemStack.is(URTags.LIGHTNING_CHASER_SADDLES);
    }

    @Override
    public boolean isHelmet(ItemStack itemStack) {
        return itemStack.is(URTags.LIGHTNING_CHASER_HELMETS);
    }

    @Override
    public boolean isChestplate(ItemStack itemStack) {
        return itemStack.is(URTags.LIGHTNING_CHASER_CHESTPLATES);
    }

    @Override
    public boolean isTailArmor(ItemStack itemStack) {
        return itemStack.is(URTags.LIGHTNING_CHASER_TAIL_ARMOR);
    }

    @Override
    public @NotNull DragonInventory createInventory() {
        return createInventory(this);
    }

    public static DragonInventory createInventory(@Nullable URDragonEntity dragon) {
        return new DragonInventory(dragon, DragonInventory.StorageSize.MEDIUM, true, true, true);
    }

    private void updateThunderstormBonus() {
        if (level().isClientSide()) return;
        if (level().getLevelData().isThundering()) {
            tryAddModifier(Attributes.ARMOR, 4, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.FLYING_SPEED, 0.2, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(Attributes.ATTACK_DAMAGE, 2f, AttributeModifier.Operation.ADD_VALUE);
            tryAddModifier(URAttributes.DRAGON_ACCELERATION_DURATION, -0.33, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_VERTICAL_SPEED, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            tryAddModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            removeModifier(Attributes.ARMOR);
            removeModifier(Attributes.FLYING_SPEED);
            removeModifier(Attributes.MOVEMENT_SPEED);
            removeModifier(Attributes.ATTACK_DAMAGE);
            removeModifier(URAttributes.DRAGON_ACCELERATION_DURATION);
            removeModifier(URAttributes.DRAGON_VERTICAL_SPEED);
            removeModifier(URAttributes.DRAGON_FLYING_ROTATION_SPEED);
            removeModifier(URAttributes.DRAGON_GROUND_ROTATION_SPEED);
        }
    }

    private void tryAddModifier(Holder<Attribute> entityAttribute, double bonus, AttributeModifier.Operation operation) {
        if (!getAttribute(entityAttribute).hasModifier(THUNDERSTORM_BONUS))
            getAttribute(entityAttribute)
                    .addTransientModifier(new AttributeModifier(THUNDERSTORM_BONUS, bonus, operation));
    }

    private void removeModifier(Holder<Attribute> entityAttribute) {
        getAttribute(entityAttribute).removeModifier(THUNDERSTORM_BONUS);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource damageSource, float amount) {
        boolean toReturn = super.hurtServer(world, damageSource, amount);
        if (getHealth() / getMaxHealth() < 0.3 && !hasSurrendered() && (getTamingProgress() <= 0 || isTame())) {
            if (!isDeadOrDying()) setHealth(getMaxHealth() * 0.3f);
            setInAirTimer(getMaxInAirTimer());
            setTarget(null);
            setSurrendered(true);
            setInAirTimer(getMaxInAirTimer());
            if (damageSource.getEntity() != null) setHomePoint(damageSource.getEntity().blockPosition());
            else setHomePoint(blockPosition());
            URPacketHelper.playSound(this, URSounds.LIGHTNING_CHASER_SURRENDER, getSoundSource(), 1, 1,1);
            if (isChallenger) bailOutTimer = 6000;
        }
        return toReturn;
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
        if (isTameable() && lightning.getCause() != null && getTamingProgress() > 0) setTamingProgress(getTamingProgress() - 1);
        addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 3));
        addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1));
        addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 1));
        addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0));
        lightning.discard();
    }

    public void triggerShoot() {
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        shootDelay = 7;
    }

    public void shoot() {
        LightningBreathEntity.createBeam(this, getShootingPointPitch(), getShootingPointYaw(), getShootingPoint().pos().add(0,  isFlying() ? -0.6 : -1.25, 0));
    }

    public float getYawProgressLimit() {
        return 45;
    }

    private void shockwave() {
        ShockwaveSphereEntity shockwaveSphereEntity = new ShockwaveSphereEntity(level());
        shockwaveSphereEntity.setOwner(this);
        shockwaveSphereEntity.setPos(position().add(0, getHeightMod(), 0));
        shockwaveSphereEntity.setDeltaMovement(Vec3.ZERO);
        shockwaveSphereEntity.setNoGravity(true);
        level().addFreshEntity(shockwaveSphereEntity);
    }

    public void triggerShockwave() {
        setSpecialAttackCooldown(getMaxSpecialAttackCooldown());
        shockwaveDelay = TRANSITION_TICKS/2;
    }

    public void meleeAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> list = world.getEntities(
                this,
                getAttackBoundingBox(),
                entity -> !getPassengers().contains(entity)
                        && !entity.is(this)
                        && !entity.getType().is(URTags.DRAGON_IMMUNE)
                        && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
        );
        Entity target = null;
        if (!list.isEmpty()) {
            target = list.getFirst();
            for (Entity entry : list) {
                if (distanceToSqr(entry) < distanceToSqr(target)) target = entry;
            }
        }
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        if (target != null && !getPassengers().contains(target)) {
            AABB targetBox = target.getBoundingBox();
            if (targetBox.intersects(getAttackBoundingBox())) doHurtTarget(world, target);
        }
    }

    @Override
    public @NotNull AABB getAttackBoundingBox() {
        Vec3 rotationVec = calculateViewVector(0, getYRot()).scale(2.5);
        return getBoundingBox().move(rotationVec);
    }

    @Override
    public boolean isSecondaryAttack() {return isFlying() ? getSecondaryAttackCooldown() > getMaxSecondaryAttackCooldown() - 24 : super.isSecondaryAttack();}

    @Override
    protected int getTicksUntilHeal() {
        return level().isThundering() ? (int) (super.getTicksUntilHeal() * 0.5) : super.getTicksUntilHeal();
    }

    @Override
    public String getDefaultVariant() {
        return "grey";
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (isTameable()) {
            if (hasSurrendered() && !getShouldBailOut() && getTamingProgress() <= 0 || player.isCreative() && getFoodItem(itemStack) != null) {
                tame(player);
                setPersistenceRequired();
                setSurrendered(false);
                shouldBailOut = false;
                isChallenger = false;
                level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                return InteractionResult.SUCCESS;
            } else if (hasSurrendered() && getTamingProgress() > 0) {
                level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force, boolean event) {
        if (hasSurrendered()) return false;
        return super.startRiding(entity, force, event);
    }

    public boolean getShouldBailOut() {
        return shouldBailOut;
    }

    public boolean isChallenger() {
        return isChallenger;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().lightningChaserMaxGroupSize * 2;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (hasSurrendered() || getShouldBailOut()) return false;
        return super.canAttack(target);
    }

    @Override
    public void setShootingPoint(ShootingPoint point) {
        shootingPoint = point;
    }

    @Override
    public ShootingPoint getShootingPoint() {
        return shootingPoint;
    }

    @Override
    public Vec3 getShootingPointAnchor() {
        return head.position().add(0, head.getBbHeight() / 2f, 0);
    }

    @Override
    public float getShootingPointDesiredPitch() {
        return getXRot();
    }

    @Override
    public float getShootingPointDesiredYaw() {
        return getYawWithAdjustment();
    }

    protected class LightningStrikeEventListener implements GameEventListener {
        private final PositionSource positionSource;
        private final int range;

        public LightningStrikeEventListener(PositionSource positionSource, int range) {
            this.positionSource = positionSource;
            this.range = range;
        }

        public @NotNull PositionSource getListenerSource() {return this.positionSource;}

        public int getListenerRadius() {return this.range;}

        @Override
        public boolean handleGameEvent(ServerLevel world, Holder<GameEvent> event, GameEvent.Context emitter, Vec3 emitterPos) {
            if (event != URGameEvents.LIGHTNING_STRIKE_FAR) return false;
            if (isTame() || getTarget() != null) return false;
            if (emitter.sourceEntity() instanceof LightningBolt lightning) {
                Player target = lightning.getCause();
                if (target != null) {
                    if (!canAttack(target)) return false;
                    setTarget(target);
                    URPacketHelper.playSound(LightningChaserEntity.this, URSounds.LIGHTNING_CHASER_ACCEPT_CHALLENGE, getSoundSource(), 1, 1,1);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> callback) {
        if (level() instanceof ServerLevel serverWorld) callback.accept(lightningStrikeEventHandler, serverWorld);
        super.updateDynamicGameEventListener(callback);
    }

    @Override
    public float getSecondsToDisableBlocking() {
        return isSecondaryAttack() || isPrimaryAttack() ? 5.0F : 0f;
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(level() instanceof ServerLevel world)) return false;
        boolean shouldBreakBlocks = isTame() ? URConfig.getConfig().lightningChaserGriefing.canTamedBreak() : URConfig.getConfig().lightningChaserGriefing.canUntamedBreak();
        return shouldBreakBlocks && world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        return isChallenger || shouldBailOut || super.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    @Override
    public boolean shouldFlyDown() {
        return super.shouldFlyDown() || (hasSurrendered() && !shouldBailOut && isFlying());
    }

    @Override
    public boolean shouldBeSaved() {
        return super.shouldBeSaved() && !shouldBailOut;
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    public void updateChildParts() {
        Vec2 wing1LeftScale;
        Vec2 wing1RightScale;
        Vec2 wing2LeftScale;
        Vec2 wing2RightScale;
        
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
                    wing1LeftScale = new Vec2(1, 1.5f);

                    wing2LeftPos = new Vector3f(2, 0, -0.5f);
                    wing2LeftScale = new Vec2(1, 1.5f);

                    wing1RightPos = new Vector3f(-2, 0, 0.5f);
                    wing1RightScale = new Vec2(1, 1.5f);

                    wing2RightPos = new Vector3f(-2, 0, -0.5f);
                    wing2RightScale = new Vec2(1, 1.5f);
                } else {
                    wing1LeftPos = new Vector3f(2.5f, 0, 0);
                    wing1LeftScale = new Vec2(1, 2.5f);

                    wing2LeftPos = new Vector3f(5, 0, 0);
                    wing2LeftScale = new Vec2(1, 2.5f);

                    wing1RightPos = new Vector3f(-2.5f, 0, 0);
                    wing1RightScale = new Vec2(1, 2.5f);

                    wing2RightPos = new Vector3f(-5, 0, 0);
                    wing2RightScale = new Vec2(1, 2.5f);
                }
                neck1Pos = new Vector3f(yawOffset * 0.25f, pitchOffset * 0.75f, 2f);
                neck2Pos = new Vector3f(yawOffset * 0.75f, pitchOffset * 1, 2.75f - Math.abs(yawOffset) * 0.25f);
                headPos = new Vector3f(yawOffset * 1.5f, pitchOffset * 1.25f, 3.5f - Math.abs(yawOffset) * 0.5f);

                tail1Pos = new Vector3f(yawOffset * 0.25f, -pitchOffset * 1, -2);
                tail2Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 1.25f, -3);
                tail3Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 1.5f , -4 + Math.abs(yawOffset) * 0.25f);
            } else {
                wing1LeftPos = new Vector3f(3, 0.75f, -0.5f);
                wing1LeftScale = new Vec2(1.5f, 3);

                wing2LeftPos = new Vector3f(3.5f, 0.75f, -1);
                wing2LeftScale = new Vec2(1.5f, 2);

                wing1RightPos = new Vector3f(-3, 0.75f, -0.5f);
                wing1RightScale = new Vec2(1.5f, 3);

                wing2RightPos = new Vector3f(-3.5f, 0.75f, -1);
                wing2RightScale = new Vec2(1.5f, 2);

                neck1Pos = new Vector3f(0, 3, 1);
                neck2Pos = new Vector3f(yawOffset * 0.5f, 3, 1.5f);
                headPos = new Vector3f(yawOffset,  3.1f, 2f);

                tail1Pos = new Vector3f(yawOffset * 0.5f, -0.5f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, -1.5f, -2.25f);
                tail3Pos = new Vector3f(yawOffset * 2f, -2.5f , -2.5f);
            }
        } else {
            if (isOrderedToSit()) {
                wing1LeftPos = new Vector3f(1.5f, 0, 0.5f);
                wing1LeftScale = new Vec2(2, 1.5f);

                wing2LeftPos = new Vector3f(1.75f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2(1.5f, 1.5f);

                wing1RightPos = new Vector3f(-1.5f, 0, 0.5f);
                wing1RightScale = new Vec2(2, 1.5f);

                wing2RightPos = new Vector3f(-1.75f, 0.75f, -0.5f);
                wing2RightScale = new Vec2(1.5f, 1.5f);

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
                wing1LeftScale = new Vec2(2, 1.5f);

                wing2LeftPos = new Vector3f(1.75f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2(1.5f, 1.5f);

                wing1RightPos = new Vector3f(-1.5f, 0, 0.5f);
                wing1RightScale = new Vec2(2, 1.5f);

                wing2RightPos = new Vector3f(-1.75f, 0.75f, -0.5f);
                wing2RightScale = new Vec2(1.5f, 1.5f);

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
