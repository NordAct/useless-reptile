package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser.LightningChaserRoamAroundGoal;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import nordmods.uselessreptile.common.gui.LightningChaserScreenHandler;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.SyncLightningBreathRotationsS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.UUID;

/*
TODO:
    Дракон:
    4) Механика вызова на бой (ебнуть по мобу с трезубца с каналом)
    ---------------------
    ---------------------
    Прочее:
    1) Возможность переключать управление поворотом дракона на полностью через камеру, частично через камеру и полностью через клавиатуру
    2) Возможность настроить оффсеты камеры для каждого вида драконов отдельно
    3) Вынести статы драконов в дарапаки
*/

public class LightningChaserEntity extends URRideableFlyingDragonEntity implements MultipartEntity {
    private int shockwaveDelay = -1;
    private int shootDelay = -1;
    private int bailOutTimer = 6000;
    private boolean shouldBailOut = false;
    private boolean isChallenger = false;
    public BlockPos roamingSpot;
    private static final UUID STORM_SPEED_BONUS = UUID.fromString("978d5bd2-71b5-4e50-8439-7c1dfa3b5089");
    private static final UUID STORM_FLYING_SPEED_BONUS = UUID.fromString("bc035ebb-3950-4001-b205-61bb4aa012f8");
    private static final UUID STORM_ARMOR_BONUS = UUID.fromString("88a1b075-ba20-436b-89ae-a564acecf338");
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

    public LightningChaserEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        baseSecondaryAttackCooldown = 30;
        basePrimaryAttackCooldown = 30;
        baseAccelerationDuration = 800;
        baseTamingProgress = 5;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        rotationSpeedGround = 6;
        rotationSpeedAir = 3;
        verticalSpeed = 0.3f;
        regenFromFood = 4;
    }

    public LightningChaserEntity(World world) {
        this(UREntities.LIGHTNING_CHASER_ENTITY, world);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new LightningChaserRoamAroundGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.EVENT) isChallenger = true;
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
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
        AnimationController<LightningChaserEntity> main = new AnimationController<>(this, "main", transitionTicks, this::main);
        AnimationController<LightningChaserEntity> turn = new AnimationController<>(this, "turn", transitionTicks, this::turn);
        AnimationController<LightningChaserEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<LightningChaserEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
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

    private <A extends GeoEntity> PlayState eye(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState main(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isSecondaryAttack()) {
                event.getController().setAnimationSpeed(calcCooldownMod());
                return loopAnim("fly.shockwave", event);
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
        if (hasSurrendered()) return loopAnim("surrender", event);
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.getController().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turn(AnimationState<A> event) {
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

    private <A extends GeoEntity> PlayState attack(AnimationState<A> event) {
        event.getController().setAnimationSpeed(1/calcCooldownMod());
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + attackType, event);
        if (isPrimaryAttack()) {
            if (isFlying()) {
                if ((isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.range.fly", event);
                return playAnim("attack.range.fly.idle", event);
            }
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    public static DefaultAttributeContainer.Builder createLightningChaserAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, getAttributeConfig().lightningChaserDamage * getAttributeConfig().dragonDamageMultiplier)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, getAttributeConfig().lightningChaserKnockback * URMobAttributesConfig.getConfig().dragonKnockbackMultiplier)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, getAttributeConfig().lightningChaserHealth * getAttributeConfig().dragonHealthMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR, getAttributeConfig().lightningChaserArmor * getAttributeConfig().dragonArmorMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, getAttributeConfig().lightningChaserArmorToughness * getAttributeConfig().dragonArmorToughnessMultiplier)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, getAttributeConfig().lightningChaserGroundSpeed * getAttributeConfig().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, getAttributeConfig().lightningChaserFlyingSpeed * getAttributeConfig().dragonFlyingSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(SURRENDERED, false);
    }
    public static final TrackedData<Boolean> SURRENDERED = DataTracker.registerData(LightningChaserEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean hasSurrendered() {return dataTracker.get(SURRENDERED);}
    public void setSurrendered(boolean state) {
        dataTracker.set(SURRENDERED, state);
        setIsSitting(state);
    }

    @Override
    public float getMaxAccelerationDuration() {
        return baseAccelerationDuration * calcSpeedMod() / (getWorld().isThundering() ? 1.5f : 1f);
    }

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
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource.isOf(DamageTypes.LIGHTNING_BOLT)) return true;
        else return super.isInvulnerableTo(damageSource);
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

        if (isFlying()) secondaryAttackDuration = 30;
        else secondaryAttackDuration = 20;

        if (shockwaveDelay == 0) {
            shockwave();
            shockwaveDelay--;
        }
        if (shockwaveDelay > -1) shockwaveDelay--;

        if (shootDelay == 0) {
            shoot();
            shootDelay--;
        }
        if (shootDelay > -1) shootDelay--;

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed && getSecondaryAttackCooldown() == 0) {
                if (isFlying()) triggerShockwave();
                else {
                    LivingEntity target = getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, getX(), getY(), getZ(), getAttackBox());
                    meleeAttack(target);
                }
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) triggerShoot();
        }

        if (!getWorld().isClient()) {
            getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(STORM_ARMOR_BONUS);
            getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).removeModifier(STORM_FLYING_SPEED_BONUS);
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(STORM_SPEED_BONUS);
            if (getWorld().getLevelProperties().isThundering()) {
                getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
                        .addTemporaryModifier(new EntityAttributeModifier(STORM_ARMOR_BONUS, "Thunderstorm bonus", 4, EntityAttributeModifier.Operation.ADDITION));
                getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED)
                        .addTemporaryModifier(new EntityAttributeModifier(STORM_FLYING_SPEED_BONUS, "Thunderstorm bonus", 0.2, EntityAttributeModifier.Operation.ADDITION));
                getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                        .addTemporaryModifier(new EntityAttributeModifier(STORM_SPEED_BONUS, "Thunderstorm bonus", 0.05, EntityAttributeModifier.Operation.ADDITION));
            }
        }

        if (!isTamed() && !shouldBailOut) {
            if (getTamingProgress() <= 0 && getHealth() / getMaxHealth() < 0.3) {
                setHealth(getMaxHealth() * 0.3f);
                setInAirTimer(getMaxInAirTimer());
                setTarget(null);
                setSurrendered(true);
                if (isChallenger) bailOutTimer = 6000;
            }

            if (hasSurrendered()) if (age % 200 == 0) heal(2);

            if (isChallenger) {
                if (bailOutTimer > 0) bailOutTimer--;
                else {
                    setSurrendered(false);
                    shouldBailOut = true;
                }
            } else if (getHealth() / getMaxHealth() > 0.5) setSurrendered(false);

            setIsSitting(hasSurrendered());
        }

        updateChildParts();
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
        Vec3d rot = getRotationVector();
        ArrayList<Integer> ids = new ArrayList<>();
        LightningBreathEntity firstSegment = null;
        float progress = rotationProgress / transitionTicks;

        for (int i = 1; i <= LightningBreathEntity.MAX_LENGTH; i++) {
            LightningBreathEntity lightningBreathEntity = new LightningBreathEntity(getWorld(), this);
            lightningBreathEntity.setPosition(head.getPos().add(rot.multiply(i)).add(0,  isFlying() ? -0.1 : -0.75, 0));
            lightningBreathEntity.setVelocity(Vec3d.ZERO);
            getWorld().spawnEntity(lightningBreathEntity);
            if (i == 1) firstSegment = lightningBreathEntity;

            ids.add(lightningBreathEntity.getId());

            boolean collides = !getWorld().isBlockSpaceEmpty(lightningBreathEntity, lightningBreathEntity.getBoundingBox()) ||
                    !getWorld().getOtherEntities(lightningBreathEntity, lightningBreathEntity.getBoundingBox(), entity -> {
                        LivingEntity owner = getOwner();
                        if (entity instanceof Tameable tameable && tameable.getOwner() == owner) return false;
                        if (getControllingPassenger() == entity) return false;
                        return entity instanceof LivingEntity;
                    }).isEmpty();
            if (collides) break;
        }

        firstSegment.setBeamLength(ids.size());

        int[] array = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) array[i] = ids.get(i);

        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos()))
                SyncLightningBreathRotationsS2CPacket.send(player, array, getPitch(), getYaw() - 45 * progress);
    }

    public void shockwave() {
        ShockwaveSphereEntity shockwaveSphereEntity = new ShockwaveSphereEntity(getWorld());
        shockwaveSphereEntity.setOwner(this);
        shockwaveSphereEntity.setPosition(getPos().add(0, getHeightMod(), 0));
        shockwaveSphereEntity.setVelocity(Vec3d.ZERO);
        shockwaveSphereEntity.setNoGravity(true);
        getWorld().spawnEntity(shockwaveSphereEntity);
    }

    public void triggerShockwave() {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        shockwaveDelay = transitionTicks;
    }

    //todo
    public void meleeAttack(LivingEntity target) {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        attackType = random.nextInt(3)+1;
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(target);
        }
    }

    @Override
    public Box getAttackBox() {
        Vec3d rotationVec = getRotationVector(0, getYaw()).multiply(2.5);
        return getBoundingBox().offset(rotationVec);
    }

    @Override
    public int getMaxSecondaryAttackCooldown() {
        return isFlying() ? super.getMaxSecondaryAttackCooldown() * 4 : super.getMaxSecondaryAttackCooldown();
    }

    @Override
    protected void updateEquipment() {
        super.updateEquipment();
        updateBanner();

        int armorBonus = 0;

        ItemStack head = inventory.getStack(1);
        ItemStack body = inventory.getStack(2);
        ItemStack tail = inventory.getStack(3);

        if (head.getItem() instanceof DragonArmorItem helmet) {
            equipStack(EquipmentSlot.HEAD, head);
            armorBonus += helmet.getArmorBonus();
        }
        if (body.getItem() instanceof DragonArmorItem chestplate) {
            equipStack(EquipmentSlot.CHEST, body);
            armorBonus += chestplate.getArmorBonus();
        }
        if (tail.getItem() instanceof DragonArmorItem tailArmor) {
            equipStack(EquipmentSlot.LEGS, tail);
            armorBonus += tailArmor.getArmorBonus();
        }

        updateArmorBonus(armorBonus);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if ((hasSurrendered() || player.isCreative() && isTamingItem(itemStack)) && !isTamed()) {
            setOwner(player);
            setSurrendered(false);
            getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            setPersistent();
            return ActionResult.SUCCESS;
        }

        if (isTamed()) {
            if (player.isSneaking() && itemStack.isEmpty() && isOwnerOrCreative(player)) {
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack){
        Item item = itemStack.getItem();
        return item.isFood() && item.getFoodComponent().isMeat();
    }

    public boolean getShouldBailOut() {
        return shouldBailOut;
    }

    public boolean isChallenger() {
        return isChallenger;
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

        float yawOffset = rotationProgress / transitionTicks;
        float pitchOffset = tiltProgress / transitionTicks;

        if (isFlying()) {
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                if (getTiltState() == 2) {
                    wing1LeftPos = new Vector3f(wing1Left.getWidth() + 0.5f, 0, 0.5f);
                    wing1LeftScale = new Vec2f(1, 1.5f);

                    wing2LeftPos = new Vector3f(wing1Left.getWidth() + 0.5f, 0, -0.5f);
                    wing2LeftScale = new Vec2f(1, 1.5f);

                    wing1RightPos = new Vector3f(-wing1Right.getWidth() - 0.5f, 0, 0.5f);
                    wing1RightScale = new Vec2f(1, 1.5f);

                    wing2RightPos = new Vector3f(-wing1Right.getWidth() - 0.5f, 0, -0.5f);
                    wing2RightScale = new Vec2f(1, 1.5f);
                } else {
                    wing1LeftPos = new Vector3f(wing1Left.getWidth(), 0, 0);
                    wing1LeftScale = new Vec2f(1, 2.5f);

                    wing2LeftPos = new Vector3f(wing1Left.getWidth() * 2, 0, 0);
                    wing2LeftScale = new Vec2f(1, 2.5f);

                    wing1RightPos = new Vector3f(-wing1Right.getWidth(), 0, 0);
                    wing1RightScale = new Vec2f(1, 2.5f);

                    wing2RightPos = new Vector3f(-wing1Right.getWidth() * 2, 0, 0);
                    wing2RightScale = new Vec2f(1, 2.5f);
                }
                neck1Pos = new Vector3f(yawOffset * 0.25f, pitchOffset * 0.75f, 2f);
                neck2Pos = new Vector3f(yawOffset * 0.75f, pitchOffset * 1, 2.75f - Math.abs(yawOffset) * 0.25f);
                headPos = new Vector3f(yawOffset * 1.5f, pitchOffset * 1.25f, 3.5f - Math.abs(yawOffset) * 0.5f);

                tail1Pos = new Vector3f(yawOffset * 0.25f, -pitchOffset * 1, -2);
                tail2Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 1.25f, -3);
                tail3Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 1.5f , -4 + Math.abs(yawOffset) * 0.25f);
            } else {
                wing1LeftPos = new Vector3f(wing1Left.getWidth(), getHeight()/4, -0.5f);
                wing1LeftScale = new Vec2f(getHeight()/2, 3);

                wing2LeftPos = new Vector3f(wing1Left.getWidth() * 1.75f, getHeight()/4, -1);
                wing2LeftScale = new Vec2f(getHeight()/2, 2);

                wing1RightPos = new Vector3f(-wing1Right.getWidth(), getHeight()/4, -0.5f);
                wing1RightScale = new Vec2f(getHeight()/2, 3);

                wing2RightPos = new Vector3f(-wing1Right.getWidth() * 1.75f, getHeight()/4, -1);
                wing2RightScale = new Vec2f(getHeight()/2, 2);

                neck1Pos = new Vector3f(0, getHeight(), 1);
                neck2Pos = new Vector3f(yawOffset * 0.5f, getHeight(), 1.5f);
                headPos = new Vector3f(yawOffset,  getHeight() + 0.1f, 2f);

                tail1Pos = new Vector3f(yawOffset * 0.5f, getHeight() - 3.5f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, getHeight() - 4.5f, -2.25f);
                tail3Pos = new Vector3f(yawOffset * 2f, getHeight() - 5.5f , -2.5f);
            }
        } else {
            if (getIsSitting()) {
                wing1LeftPos = new Vector3f(getWidth() / 2, 0, 0.5f);
                wing1LeftScale = new Vec2f(getHeight() - 1, getWidth() / 2f);

                wing2LeftPos = new Vector3f(getWidth() / 2 + 0.25f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2f(getHeight() - 1.5f, getWidth() / 2f);

                wing1RightPos = new Vector3f(-getWidth() / 2, 0, 0.5f);
                wing1RightScale = new Vec2f(getHeight() - 1, getWidth() / 2f);

                wing2RightPos = new Vector3f(-getWidth() / 2 - 0.25f, 0.75f, -0.5f);
                wing2RightScale = new Vec2f(getHeight() - 1.5f, getWidth() / 2f);

                if (hasSurrendered()) {
                    neck1Pos = new Vector3f(0, 1.6f, 1);
                    neck2Pos = new Vector3f(yawOffset * 0.4f, 1.3f, 1.7f);
                    headPos = new Vector3f(yawOffset * 0.8f,  0.5f, 2.4f);
                } else {
                    neck1Pos = new Vector3f(0, getHeight() - 0.5f, 1);
                    neck2Pos = new Vector3f(yawOffset * 0.4f, getHeight() - 0.2f, 1.5f);
                    headPos = new Vector3f(yawOffset * 0.8f, getHeight() + 0.1f, 2f);
                }

                tail1Pos = new Vector3f(0, 0.3f, -getWidth() + 0.8f);
                tail2Pos = new Vector3f(0, 0.35f, -getWidth() - 0.2f);
                tail3Pos = new Vector3f(0, 0.4f , -getWidth() - 1.2f);

            } else {
                wing1LeftPos = new Vector3f(getWidth() / 2, 0, 0.5f);
                wing1LeftScale = new Vec2f(getHeight() - 1, getWidth() / 2f);

                wing2LeftPos = new Vector3f(getWidth() / 2 + 0.25f, 0.75f, -0.5f);
                wing2LeftScale = new Vec2f(getHeight() - 1.5f, getWidth() / 2f);

                wing1RightPos = new Vector3f(-getWidth() / 2, 0, 0.5f);
                wing1RightScale = new Vec2f(getHeight() - 1, getWidth() / 2f);

                wing2RightPos = new Vector3f(-getWidth() / 2 - 0.25f, 0.75f, -0.5f);
                wing2RightScale = new Vec2f(getHeight() - 1.5f, getWidth() / 2f);

                neck1Pos = new Vector3f(0, getHeight() - 1f, 1);
                neck2Pos = new Vector3f(yawOffset * 0.4f, getHeight() - 0.75f, 1.5f);
                headPos = new Vector3f(yawOffset * 0.8f,  getHeight() - 0.4f, 2f);

                tail1Pos = new Vector3f(yawOffset * 0.2f, getHeight() - 1.5f, -getWidth() + 0.9f);
                tail2Pos = new Vector3f(yawOffset * 0.4f, getHeight() - 0.8f,  -getWidth() + 0.2f);
                tail3Pos = new Vector3f(yawOffset * 0.8f, getHeight() - 0.5f , -getWidth() - 0.7f);
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
        neck1.setRelativePos(neck1Pos);
        neck2.setRelativePos(neck2Pos);

        tail1.setRelativePos(tail1Pos);
        tail2.setRelativePos(tail2Pos);
        tail3.setRelativePos(tail3Pos);
    }
}
