package nordmods.uselessreptile.common.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawEscapeLightGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawUntamedTargetGoal;
import nordmods.uselessreptile.common.entity.ai.navigation.MoleclawNavigation;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.event.MoleclawGetBlockMiningLevelEvent;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

public class MoleclawEntity extends URRideableDragonEntity {
    public int attackDelay = 0;
    public static final float defaultWidth = 2f;
    public static final float defaultHeight = 2.9f;
    private int panicSoundDelay = 0;

    public static final float BASE_GROUND_SPEED = 0.25f;

    public MoleclawEntity(EntityType<? extends URRideableDragonEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;
        navigation = new MoleclawNavigation(this, world);

        pitchLimitGround = 50;
        baseTamingProgress = 64;
        ticksUntilHeal = 400;
        inventory = new DragonInventory(DragonInventory.StorageSize.LARGE, true, true, true);
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.SKY, pos) > 0 || world.getLightLevel(LightType.BLOCK, pos) > 0) return false;
        return URDragonEntity.canDragonSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new MoleclawEscapeLightGoal(this));
        goalSelector.add(2, new DragonCallBackGoal(this));
        goalSelector.add(3, new SitGoal(this));
        goalSelector.add(4, new DragonConsumeFoodFromInventoryGoal(this));
        goalSelector.add(8, new MoleclawAttackGoal(this, 512));
        goalSelector.add(9, new DragonReturnToHomePoint(this));
        goalSelector.add(10, new DragonWanderAroundGoal(this));
        goalSelector.add(11, new DragonLookAroundGoal(this));
        targetSelector.add(5, new MoleclawUntamedTargetGoal<>(this, PlayerEntity.class));
        targetSelector.add(6, new MoleclawUntamedTargetGoal<>(this, ChickenEntity.class));
        targetSelector.add(5, new AttackWithOwnerGoal(this));
        targetSelector.add(6, new TrackOwnerAttackerGoal(this));
        targetSelector.add(4, new DragonRevengeGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_PANICKING, false);
    }
    public static final TrackedData<Boolean> IS_PANICKING = DataTracker.registerData(MoleclawEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean isPanicking() {return dataTracker.get(IS_PANICKING);}
    public void setIsPanicking (boolean state) {dataTracker.set(IS_PANICKING, state);}

    public static DefaultAttributeContainer.Builder createMoleclawAttributes() {
        return createDragonAttributes()
                .add(EntityAttributes.ATTACK_DAMAGE, attributes().moleclawDamage)
                .add(EntityAttributes.ATTACK_KNOCKBACK, attributes().moleclawKnockback)
                .add(EntityAttributes.MAX_HEALTH, attributes().moleclawHealth)
                .add(EntityAttributes.ARMOR, attributes().moleclawArmor)
                .add(EntityAttributes.ARMOR_TOUGHNESS, attributes().moleclawArmorToughness)
                .add(EntityAttributes.MOVEMENT_SPEED, attributes().moleclawGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().moleclawRotationSpeedGround)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().moleclawBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().moleclawBaseSecondaryAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().moleclawRegenerationFromFood)
                .add(URAttributes.MOLECLAW_MINING_LEVEL, 0);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<MoleclawEntity> main = new AnimationController<>("main", TRANSITION_TICKS, this::mainController);
        AnimationController<MoleclawEntity> turn = new AnimationController<>("turn", TRANSITION_TICKS, this::turnController);
        AnimationController<MoleclawEntity> attack = new AnimationController<>( "attack", 0, this::attackController);
        AnimationController<MoleclawEntity> eye = new AnimationController<>("eye", 0, this::eyeController);
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
        event.controller().setAnimationSpeed(animationSpeed);
        if (getIsSitting() && !isDancing() && !isPanicking()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed() || isMovingBackwards()) {
            if (isPanicking()) return loopAnim("panic", event);
            return loopAnim("walk", event);
        }
        event.controller().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
        if (isPanicking()) return loopAnim("panic.idle", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turnController(AnimationTest<A> event) {
        byte turnState = getTurningState();
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attackController(AnimationTest<A> event){
        event.controller().setAnimationSpeed(1/ getCooldownModifier());
        if (isSecondaryAttack()) return playAnim( "attack.normal" + getAttackType(), event);
        if (isPrimaryAttack()) {
            if (isPanicking()) return playAnim( "attack.strong.panic", event);
            return playAnim( "attack.strong", event);
        }
        return playAnim("attack.none", event);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getIsSitting()) setHitboxModifiers(1, 1, 2.5f);
        else setHitboxModifiers(0.75f, 1f, 2.5f);
        tryPanic();

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed() && getSecondaryAttackCooldown() == 0) scheduleNormalAttack();
            if (isPrimaryAttackPressed() && getPrimaryAttackCooldown() == 0) scheduleStrongAttack();
        }

        if (attackDelay > 0) {
            attackDelay++;
            if (attackDelay > TRANSITION_TICKS + 1) {
                if (isPrimaryAttack()) strongAttack();
                if (isSecondaryAttack()) meleeAttack();
                attackDelay = 0;
            }
        }
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isSaddleItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.MOLECLAW_SADDLES);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
        return MoleclawScreenHandler.createScreenHandler(syncId, inv, inventory);
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
                setTamedBy(player);
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            } else {
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
            }
            setPersistent();
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    public void meleeAttack() {
        if (!(getWorld() instanceof ServerWorld world)) return;
        List<Entity> targets = world.getOtherEntities(this, getAttackBox(), livingEntity -> !getPassengerList().contains(livingEntity));
        if (!targets.isEmpty()) for (Entity mob: targets) {
            Box targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getAttackBox())) tryAttack(world, mob);
        }
    }

    public void strongAttack() {
        if (!(getWorld() instanceof ServerWorld world)) return;
        List<Entity> targets = getWorld().getOtherEntities(this, getSecondaryAttackBox(), livingEntity -> !getPassengerList().contains(livingEntity));
        if (!targets.isEmpty()) for (Entity mob : targets) {
            Box targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getSecondaryAttackBox())) tryAttack(world, mob);
        }

        if (!canBreakBlocks()) return;

        Box box = getSecondaryAttackBox();
        Iterable<BlockPos> blocks = BlockPos.iterate((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ);
        float maxMiningLevel = (float) getAttributeValue(URAttributes.MOLECLAW_MINING_LEVEL);
        if (hasStatusEffect(StatusEffects.STRENGTH)) maxMiningLevel += getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + 1;
        if (hasStatusEffect(StatusEffects.WEAKNESS)) maxMiningLevel -= getStatusEffect(StatusEffects.WEAKNESS).getAmplifier() + 1;
        for (BlockPos blockPos : blocks) {
            if (isBlockProtected(blockPos)) continue;

            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock().getHardness() < 0) continue;

            float miningLevel = MoleclawGetBlockMiningLevelEvent.EVENT.invoker().getMiningLevel(blockState);
            if (!blockState.isAir() && miningLevel <= maxMiningLevel) {
                boolean shouldDrop = getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                world.breakBlock(blockPos, shouldDrop, this);
            }
        }
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(getWorld() instanceof ServerWorld world)) return false;
        boolean shouldBreakBlocks = isTamed() ? URConfig.getConfig().moleclawGriefing.canTamedBreak() : URConfig.getConfig().moleclawGriefing.canUntamedBreak();
        return shouldBreakBlocks &&  world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    public Box getAttackBox() {
        Vec3d rotationVec = getRotationVector(0, getYaw());
        double x = rotationVec.x * 2;
        double z = rotationVec.z * 2;
        return new Box(getPos().getX() + x - 1.5, getPos().getY(), getPos().getZ() + z - 1.5,
                getPos().getX() + x + 1.5, getPos().getY() + getHeight(), getPos().getZ() + z + 1.5);
    }

    @Override
    public Box getSecondaryAttackBox() {
        double x = -Math.sin(Math.toRadians(getYaw())) * 2;
        double y = -Math.sin(Math.toRadians(getPitch()));
        double z = Math.cos(Math.toRadians(getYaw())) * 2;
        double heightIncrease = canBeControlledByRider() ? 2 : 1;
        return new Box(getPos().getX() + x - 1.25, getPos().getY() + y + 0.5, getPos().getZ() + z - 1.25,
                getPos().getX() + x + 1.25, getPos().getY() + getHeight() + heightIncrease + y, getPos().getZ() + z + 1.25);
    }

    @Override
    public String getDefaultVariant() {
        return "black";
    }

    public void tryPanic() {
        playPanicSound();
        if (!hasLightProtection()) setIsPanicking(isTooBrightAtPos(getBlockPos()));
        else setIsPanicking(false);
    }

    public boolean hasLightProtection() {
        return getEquippedStack(EquipmentSlot.HEAD).isIn(URTags.PROTECTS_MOLECLAW_FROM_LIGHT);
    }

    public boolean isTooBrightAtPos(BlockPos blockPos) {
        return !hasLightProtection() && getLightAtPos(blockPos, this) > 7;
    }

    public static int getLightAtPos(BlockPos blockPos, LivingEntity entity) {
        World world = entity.getWorld();
        int lightLevelBlock = world.getLightLevel(LightType.BLOCK, blockPos);
        int lightLevelSky = world.getLightLevel(LightType.SKY, blockPos);
        long timeOfDay = world.getTimeOfDay() % 24000;
        boolean isDayTime = (timeOfDay < 13000 || timeOfDay > 23000) && !world.getDimension().hasFixedTime();
        return Math.max(lightLevelBlock, isDayTime ? lightLevelSky : 0);
    }

    @Override
    public double getSwimHeight() {
        return 1;
    }

    @Override
    public boolean canBeControlledByRider() {
        return super.canBeControlledByRider() && !isPanicking();
    }

    public void scheduleNormalAttack() {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        if (attackDelay == 0) attackDelay = 6;
        setAttackType(random.nextInt(2)+1);
    }

    public void scheduleStrongAttack() {
        if (attackDelay == 0) attackDelay = 6;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
    }

    @Override
    public float getWeaponDisableBlockingForSeconds() {
        return isPrimaryAttack() ? 5.0F : 1F;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return -world.getPhototaxisFavor(pos);
    }

    private void playPanicSound() {
        if (isPanicking()) {
            if (panicSoundDelay == 0) {
                SoundInfo soundInfo = getSoundInfo("panic");
                if (soundInfo != null) playSound(SoundEvent.of(soundInfo.id()), soundInfo.volume() ,soundInfo.pitch());
                panicSoundDelay = random.nextInt(41) + 60;
            }
            else panicSoundDelay--;
        } else panicSoundDelay = 2;
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack){
        return itemStack.isIn(URTags.MOLECLAW_FOOD);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack){
        return itemStack.isIn(URTags.MOLECLAW_TAMING_ITEM);
    }

    @Override
    public int getLimitPerChunk() {
        return URConfig.getConfig().moleclawMaxGroupSize * 2;
    }

    @Override
    protected boolean canTeleportTo(BlockPos pos) {
        if (isTooBrightAtPos(pos)) return false;
        return super.canTeleportTo(pos);
    }
}
