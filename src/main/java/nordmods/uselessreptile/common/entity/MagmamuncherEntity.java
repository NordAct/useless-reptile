package nordmods.uselessreptile.common.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherApplyFireResistanceGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherEatMagmaGoal;
import nordmods.uselessreptile.common.entity.ai.navigation.MagmamuncherNavigation;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URBlocks;
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

public class MagmamuncherEntity extends URDragonEntity implements HeadMountDragon {
    public static final float BASE_GROUND_SPEED = 0.2f;
    public static int EAT_MAGMA_COOLDOWN_AVERAGE = 20*50;
    public int eatMagmaCooldown = 0;
    private int eatingMagmaProgress;
    private static int MAX_EATING_MAGMA_PROGRESS = 20*5;
    public static final float DISTANCE_TO_EAT = 1.25f;
    public static final RegistryKey<LootTable> MAGMA_EATEN_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, UselessReptile.id("entities/magmamuncher_from_magma"));

    public MagmamuncherEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        baseTamingProgress = 12;
        sprintSpeedModifier = 1.3f;
        setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0);
        setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0);
        navigation = new MagmamuncherNavigation(this, getWorld());
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack) {
        return itemStack.isIn(URTags.MAGMAMUNCHER_FOOD);
    }

    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        return itemStack.isIn(URTags.MAGMAMUNCHER_TAMING_ITEM);
    }

    @Override
    public String getDefaultVariant() {
        return "netherrack";
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
        return new URDragonScreenHandler(URScreenHandlers.MAGMAMUNCHER_INVENTORY, syncId, inv, getInventory());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        AnimationController<RiverPikehornEntity> main = new AnimationController<>("main", TRANSITION_TICKS, this::mainController);
        AnimationController<RiverPikehornEntity> turn = new AnimationController<>( "turn", TRANSITION_TICKS, this::turnController);
        AnimationController<RiverPikehornEntity> attack = new AnimationController<>("attack", 0, this::attackController);
        AnimationController<RiverPikehornEntity> eye = new AnimationController<>("eye", 0, this::eyeController);
        main.setSoundKeyframeHandler(this::soundHandler);
        attack.setSoundKeyframeHandler(this::soundHandler);
        turn.setSoundKeyframeHandler(this::soundHandler);
        eye.setSoundKeyframeHandler(this::soundHandler);
        controllerRegistrar.add(main, turn, attack, eye);
    }

    private <A extends GeoEntity> PlayState eyeController(AnimationTest<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState mainController(AnimationTest<A> event) {
        event.controller().transitionLength((int) (TRANSITION_TICKS / event.controller().getAnimationSpeed()));
        event.controller().setAnimationSpeed(animationSpeed);
        if (hasVehicle()) return loopAnim("sit.head", event);
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving()) return loopAnim("walk", event);
        event.controller().setAnimationSpeed(1);
        if (isEatingMagma()) return loopAnim("eat", event);
        if (isDancing()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turnController(AnimationTest<A> event) {
        byte turnState = getTurningState();
        if (event.isMoving()) {
            if (turnState == 1) return loopAnim("turn.walk.left", event);
            if (turnState == 2) return loopAnim("turn.walk.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attackController(AnimationTest<A> event) {
        event.controller().setAnimationSpeed(1 / getCooldownModifier());
        if (isPrimaryAttack()) return playAnim( "attack" + getAttackType(), event);
        return playAnim("attack.none", event);
    }

    public static DefaultAttributeContainer.Builder createMagmamuncherAttributes() {
        return createDragonAttributes()
                .add(EntityAttributes.ATTACK_DAMAGE, attributes().magmamuncherDamage)
                .add(EntityAttributes.ATTACK_KNOCKBACK, attributes().magmamuncherKnockback)
                .add(EntityAttributes.MAX_HEALTH, attributes().magmamuncherHealth)
                .add(EntityAttributes.ARMOR, attributes().magmamuncherArmor)
                .add(EntityAttributes.ARMOR_TOUGHNESS, attributes().magmamuncherArmorToughness)
                .add(EntityAttributes.MOVEMENT_SPEED, attributes().magmamuncherGroundSpeed)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().magmamuncherRotationSpeedGround)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().magmamuncherBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().magmamuncherRegenerationFromFood);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(EATING_MAGMA, false);
        builder.add(MAGMA_POS, BlockPos.ORIGIN);
    }
    public static final TrackedData<Boolean> EATING_MAGMA = DataTracker.registerData(MagmamuncherEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<BlockPos> MAGMA_POS = DataTracker.registerData(MagmamuncherEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public boolean isEatingMagma() {return dataTracker.get(EATING_MAGMA);}
    public void setEatingMagma(boolean state) {dataTracker.set(EATING_MAGMA, state);}

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(1, new DragonCallBackGoal(this));
        goalSelector.add(2, new SitGoal(this));
        goalSelector.add(3, new MagmamuncherApplyFireResistanceGoal(this));
        goalSelector.add(4, new DragonConsumeItemFromInventoryGoal(this));
        goalSelector.add(5, new MagmamuncherAttackGoal(this, 4096));
        goalSelector.add(6, new MagmamuncherEatMagmaGoal(this));
        goalSelector.add(8, new DragonWanderAroundGoal(this));
        goalSelector.add(9, new DragonLookAroundGoal(this));
        targetSelector.add(1, new DragonRevengeGoal(this));
        targetSelector.add(2, new AttackWithOwnerGoal(this));
        targetSelector.add(3, new TrackOwnerAttackerGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.add(4, new UntamedActiveTargetGoal<>(this, PlayerEntity.class, true, null));
        targetSelector.add(5, new ActiveTargetGoal<>(this, MagmaCubeEntity.class, true, null));
    }

    @Override
    public void writeCustomData(WriteView tag) {
        super.writeCustomData(tag);
        tag.putInt("EatMagmaCooldown", eatMagmaCooldown);
    }

    @Override
    public void readCustomData(ReadView tag) {
        super.readCustomData(tag);
        eatMagmaCooldown = tag.getInt("EatMagmaCooldown", EAT_MAGMA_COOLDOWN_AVERAGE);
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof PlayerEntity) setHitboxModifiers(0.35f, 0.6f, 0);
        else setHitboxModifiers(0.35f, 0.7f, 0);
        if (eatMagmaCooldown > 0) eatMagmaCooldown--;
        checkIfEatingMagma();
    }

    private void checkIfEatingMagma() {
        if (isEatingMagma()) {
            BlockPos pos = getMagmaBlockPos();
            if (!getWorld().isClient() &&
                    (getWorld().getBlockState(pos).getBlock() != Blocks.MAGMA_BLOCK
                            || pos.toCenterPos().squaredDistanceTo(getPos()) >= DISTANCE_TO_EAT * DISTANCE_TO_EAT)) {
                setEatingMagma(false);
            } else if (!getWorld().isClient() && ++eatingMagmaProgress >= MAX_EATING_MAGMA_PROGRESS) {
                setEatingMagma(false);
                eatMagmaCooldown = MagmamuncherEntity.EAT_MAGMA_COOLDOWN_AVERAGE + getRandom().nextBetween(-20 * 10, 20 * 10);
                eatingMagmaProgress = 0;
                getWorld().setBlockState(pos, URBlocks.DEPLETED_MAGMA.getDefaultState());
                getWorld().playSoundClient(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, SoundEvents.BLOCK_NETHERRACK_BREAK, getSoundCategory(), 1, 1, true);
                forEachGiftedItem((ServerWorld) getWorld(), MAGMA_EATEN_TABLE, this::dropStack);
            } else if (getWorld().isClient() && age % 10 == 0) {
                getWorld().addBlockBreakParticles(pos, Blocks.MAGMA_BLOCK.getDefaultState());
                getWorld().playSoundClient(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, SoundEvents.BLOCK_NETHERRACK_HIT, getSoundCategory(), 1, 1, true);
            }
        } else {
            eatingMagmaProgress = 0;
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTameable() && isTamingItem(itemStack)) {
            player.setStackInHand(hand, consumeGivenItem(player, itemStack, SoundEvents.ENTITY_GENERIC_EAT.value()));
            if (random.nextInt(5) == 0) setTamingProgress(getTamingProgress() - 3);
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

    public void attackMelee(LivingEntity target) {
        if (!(getWorld() instanceof ServerWorld world)) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        EntityAttributeModifier modifier = new EntityAttributeModifier(UselessReptile.id("magma_cube_bonus"), 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (target instanceof MagmaCubeEntity) getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).addTemporaryModifier(modifier);
        if (tryAttack(world, target)) {
            target.setOnFireFor((float) (0.75f * getAttributeValue(EntityAttributes.ATTACK_DAMAGE)));
        }
        getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).removeModifier(modifier);
    }

    public BlockPos getMagmaBlockPos() {
        return dataTracker.get(MAGMA_POS);
    }

    public void setMagmaBlockPos(BlockPos magmaBlockPos) {
        dataTracker.set(MAGMA_POS, magmaBlockPos);
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(getWorld() instanceof ServerWorld world)) return false;
        boolean shouldBreakBlocks = isTamed() ? URConfig.getConfig().magmamuncherGriefing.canTamedBreak() : URConfig.getConfig().magmamuncherGriefing.canUntamedBreak();
        return shouldBreakBlocks &&  world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
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
        return createInventory(this);
    }

    public static DragonInventory createInventory(@Nullable URDragonEntity dragon) {
        return new DragonInventory(dragon, DragonInventory.StorageSize.SMALL, false, false, false);
    }

}
