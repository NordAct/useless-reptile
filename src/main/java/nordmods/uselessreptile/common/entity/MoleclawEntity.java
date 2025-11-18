package nordmods.uselessreptile.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URScreenHandlers;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    public MoleclawEntity(EntityType<? extends URRideableDragonEntity> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;
        navigation = new MoleclawNavigation(this, world);

        pitchLimitGround = 50;
        ticksUntilHeal = 400;
    }

    public static boolean canDragonSpawn(EntityType<? extends Mob> type, LevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        if (world.getBrightness(LightLayer.SKY, pos) > 0 || world.getBrightness(LightLayer.BLOCK, pos) > 0) return false;
        return URDragonEntity.canDragonSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MoleclawEscapeLightGoal(this));
        goalSelector.addGoal(2, new DragonCallBackGoal(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal(this));
        goalSelector.addGoal(8, new MoleclawAttackGoal(this, 512));
        goalSelector.addGoal(9, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(10, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(11, new DragonLookAroundGoal(this));
        targetSelector.addGoal(5, new MoleclawUntamedTargetGoal<>(this, Player.class));
        targetSelector.addGoal(6, new MoleclawUntamedTargetGoal<>(this, Chicken.class));
        targetSelector.addGoal(5, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(6, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new DragonRevengeGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PANICKING, false);
    }
    public static final EntityDataAccessor<Boolean> IS_PANICKING = SynchedEntityData.defineId(MoleclawEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean isPanicking() {return entityData.get(IS_PANICKING);}
    public void setIsPanicking (boolean state) {entityData.set(IS_PANICKING, state);}

    public static AttributeSupplier.Builder createMoleclawAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().moleclawDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().moleclawKnockback)
                .add(Attributes.MAX_HEALTH, attributes().moleclawHealth)
                .add(Attributes.ARMOR, attributes().moleclawArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().moleclawArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().moleclawGroundSpeed)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().moleclawRotationSpeedGround)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().moleclawBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().moleclawBaseSecondaryAttackCooldown)
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
        event.controller().transitionLength((int) (TRANSITION_TICKS / event.controller().getAnimationSpeed()));
        event.controller().setAnimationSpeed(animationSpeed);
        if (isOrderedToSit() && !isDancing() && !isPanicking()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed() || isMovingBackwards()) {
            if (isPanicking()) return loopAnim("panic", event);
            return loopAnim("walk", event);
        }
        event.controller().setAnimationSpeed(1);
        if (isDancing() && !isVehicle()) return loopAnim("dance", event);
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
        if (!isOrderedToSit()) setHitboxModifiers(1, 1, 2.5f);
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
    public boolean isSaddle(ItemStack itemStack) {
        return itemStack.is(URTags.MOLECLAW_SADDLES);
    }

    @Override
    public boolean isHelmet(ItemStack itemStack) {
        return itemStack.is(URTags.MOLECLAW_HELMETS);
    }

    @Override
    public boolean isChestplate(ItemStack itemStack) {
        return itemStack.is(URTags.MOLECLAW_CHESTPLATES);
    }

    @Override
    public boolean isTailArmor(ItemStack itemStack) {
        return itemStack.is(URTags.MOLECLAW_TAIL_ARMOR);
    }

    @Override
    public @NotNull DragonInventory createInventory() {
        return createInventory(this);
    }

    public static DragonInventory createInventory(@Nullable URDragonEntity dragon) {
        return new DragonInventory(dragon, DragonInventory.StorageSize.LARGE, true, true, true);
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (!level().isClientSide()) GUIEntityToRenderS2CPacket.send((ServerPlayer) player, this);
        return new URDragonScreenHandler(URScreenHandlers.MOLECLAW_INVENTORY, syncId, inv, getInventory());
    }

    public void meleeAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> targets = level()
                .getEntities(
                        this,
                        getAttackBoundingBox(),
                        entity -> !getPassengers().contains(entity)
                                && !entity.getType().is(URTags.DRAGON_IMMUNE)
                                && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
                );
        if (!targets.isEmpty()) for (Entity mob: targets) {
            AABB targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getAttackBoundingBox())) doHurtTarget(world, mob);
        }
    }

    public void strongAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> targets = level()
                .getEntities(
                        this,
                        getSecondaryAttackBox(),
                        entity -> !getPassengers().contains(entity)
                                && !entity.getType().is(URTags.DRAGON_IMMUNE)
                                && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
                );
        if (!targets.isEmpty()) for (Entity mob : targets) {
            AABB targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getSecondaryAttackBox())) doHurtTarget(world, mob);
        }

        if (!canBreakBlocks()) return;

        Iterable<BlockPos> blocks = BlockPos.betweenClosed(getSecondaryAttackBox());
        float maxMiningLevel = (float) getAttributeValue(URAttributes.MOLECLAW_MINING_LEVEL);
        if (hasEffect(MobEffects.STRENGTH)) maxMiningLevel += getEffect(MobEffects.STRENGTH).getAmplifier() + 1;
        if (hasEffect(MobEffects.WEAKNESS)) maxMiningLevel -= getEffect(MobEffects.WEAKNESS).getAmplifier() + 1;
        for (BlockPos blockPos : blocks) {
            if (isBlockProtected(blockPos)) continue;

            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock().defaultDestroyTime() < 0) continue;

            float miningLevel = MoleclawGetBlockMiningLevelEvent.EVENT.invoker().getMiningLevel(blockState);
            if (!blockState.isAir() && miningLevel <= maxMiningLevel) {
                boolean shouldDrop = getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                world.destroyBlock(blockPos, shouldDrop, this);
            }
        }
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(level() instanceof ServerLevel world)) return false;
        boolean shouldBreakBlocks = isTame() ? URConfig.getConfig().moleclawGriefing.canTamedBreak() : URConfig.getConfig().moleclawGriefing.canUntamedBreak();
        return shouldBreakBlocks &&  world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
    }

    @Override
    public @NotNull AABB getAttackBoundingBox() {
        Vec3 rotationVec = calculateViewVector(0, getYRot());
        double x = rotationVec.x * 2;
        double z = rotationVec.z * 2;
        return new AABB(position().x() + x - 1.5, position().y(), position().z() + z - 1.5,
                position().x() + x + 1.5, position().y() + getBbHeight(), position().z() + z + 1.5);
    }

    @Override
    public AABB getSecondaryAttackBox() {
        double halfWidth = getBbWidth() / 2f;
        double x = -Math.sin(Math.toRadians(getYRot())) * halfWidth;
        double y = 0;
        if (canBeControlledByRider()) {
            if (getXRot() > 25) y = -1;
            if (getXRot() < -25) y = 1;
        } else y = -Math.sin(Math.toRadians(getXRot()));
        double z = Math.cos(Math.toRadians(getYRot())) * halfWidth;
        double heightIncrease = canBeControlledByRider() ? 2 : 1;
        return new AABB(position().x() + x - 1.25, position().y() + y, position().z() + z - 1.25,
                position().x() + x + 1.25, position().y() + getBbHeight() + heightIncrease + y, position().z() + z + 1.25);
    }

    @Override
    public String getDefaultVariant() {
        return "black";
    }

    public void tryPanic() {
        playPanicSound();
        if (!hasLightProtection()) setIsPanicking(isTooBrightAtPos(blockPosition()));
        else setIsPanicking(false);
    }

    public boolean hasLightProtection() {
        return getItemBySlot(EquipmentSlot.HEAD).is(URTags.PROTECTS_MOLECLAW_FROM_LIGHT);
    }

    public boolean isTooBrightAtPos(BlockPos blockPos) {
        return !hasLightProtection() && getLightAtPos(blockPos, this) > 7;
    }

    public static int getLightAtPos(BlockPos blockPos, LivingEntity entity) {
        Level world = entity.level();
        int lightLevelBlock = world.getBrightness(LightLayer.BLOCK, blockPos);
        int lightLevelSky = world.getBrightness(LightLayer.SKY, blockPos);
        long timeOfDay = world.getDayTime() % 24000;
        boolean isDayTime = (timeOfDay < 13000 || timeOfDay > 23000) && !world.dimensionType().hasFixedTime();
        return Math.max(lightLevelBlock, isDayTime ? lightLevelSky : 0);
    }

    @Override
    public double getFluidJumpThreshold() {
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
    public float getSecondsToDisableBlocking() {
        return isPrimaryAttack() ? 5.0F : 1F;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) {
        return -world.getPathfindingCostFromLightLevels(pos);
    }

    private void playPanicSound() {
        if (isPanicking()) {
            if (panicSoundDelay == 0) {
                SoundInfo soundInfo = getSoundInfo("panic");
                if (soundInfo != null) playSound(SoundEvent.createVariableRangeEvent(soundInfo.id()), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()));
                panicSoundDelay = random.nextInt(41) + 60;
            }
            else panicSoundDelay--;
        } else panicSoundDelay = 2;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().moleclawMaxGroupSize * 2;
    }

    @Override
    protected boolean canTeleportTo(BlockPos pos) {
        if (isTooBrightAtPos(pos)) return false;
        return super.canTeleportTo(pos);
    }

    @Override
    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        return isPanicking() || super.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }
}
