package nordmods.uselessreptile.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherApplyFireResistanceGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherEatMagmaGoal;
import nordmods.uselessreptile.common.entity.ai.navigation.MagmamuncherNavigation;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.init.URScreenHandlers;
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
    public static final ResourceKey<LootTable> MAGMA_EATEN_TABLE = ResourceKey.create(Registries.LOOT_TABLE, UselessReptile.id("entities/magmamuncher_from_magma"));

    public MagmamuncherEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        sprintSpeedModifier = 1.3f;
        setPathfindingMalus(PathType.DAMAGE_FIRE, 0);
        setPathfindingMalus(PathType.DANGER_FIRE, 0);
        navigation = new MagmamuncherNavigation(this, level());
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public String getDefaultVariant() {
        return "netherrack";
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (!level().isClientSide()) GUIEntityToRenderS2CPacket.send((ServerPlayer) player, this);
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
        if (isPassenger()) return loopAnim("sit.head", event);
        if (isOrderedToSit() && !isDancing()) return loopAnim("sit", event);
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

    public static AttributeSupplier.Builder createMagmamuncherAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().magmamuncherDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().magmamuncherKnockback)
                .add(Attributes.MAX_HEALTH, attributes().magmamuncherHealth)
                .add(Attributes.ARMOR, attributes().magmamuncherArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().magmamuncherArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().magmamuncherGroundSpeed)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().magmamuncherRotationSpeedGround)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().magmamuncherBasePrimaryAttackCooldown);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EATING_MAGMA, false);
        builder.define(MAGMA_POS, BlockPos.ZERO);
    }
    public static final EntityDataAccessor<Boolean> EATING_MAGMA = SynchedEntityData.defineId(MagmamuncherEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> MAGMA_POS = SynchedEntityData.defineId(MagmamuncherEntity.class, EntityDataSerializers.BLOCK_POS);
    public boolean isEatingMagma() {return entityData.get(EATING_MAGMA);}
    public void setEatingMagma(boolean state) {entityData.set(EATING_MAGMA, state);}

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new DragonCallBackGoal(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(3, new MagmamuncherApplyFireResistanceGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal(this));
        goalSelector.addGoal(5, new MagmamuncherAttackGoal(this, 4096));
        goalSelector.addGoal(6, new MagmamuncherEatMagmaGoal(this));
        goalSelector.addGoal(8, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(9, new DragonLookAroundGoal(this));
        targetSelector.addGoal(1, new DragonRevengeGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, MagmaCube.class, true, null));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EatMagmaCooldown", eatMagmaCooldown);
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        eatMagmaCooldown = tag.getIntOr("EatMagmaCooldown", EAT_MAGMA_COOLDOWN_AVERAGE);
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof Player) setHitboxModifiers(0.35f, 0.6f, 0);
        else setHitboxModifiers(0.35f, 0.7f, 0);
        if (eatMagmaCooldown > 0) eatMagmaCooldown--;
        checkIfEatingMagma();
    }

    private void checkIfEatingMagma() {
        if (isEatingMagma()) {
            BlockPos pos = getMagmaBlockPos();
            if (!level().isClientSide() &&
                    (level().getBlockState(pos).getBlock() != Blocks.MAGMA_BLOCK
                            || pos.getCenter().distanceToSqr(position()) >= DISTANCE_TO_EAT * DISTANCE_TO_EAT)) {
                setEatingMagma(false);
            } else if (!level().isClientSide() && ++eatingMagmaProgress >= MAX_EATING_MAGMA_PROGRESS) {
                setEatingMagma(false);
                eatMagmaCooldown = MagmamuncherEntity.EAT_MAGMA_COOLDOWN_AVERAGE + getRandom().nextIntBetweenInclusive(-20 * 10, 20 * 10);
                eatingMagmaProgress = 0;
                level().setBlockAndUpdate(pos, URBlocks.DEPLETED_MAGMA.defaultBlockState());
                level().playLocalSound(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, SoundEvents.NETHERRACK_BREAK, getSoundSource(), 1, 1, true);
                dropFromGiftLootTable((ServerLevel) level(), MAGMA_EATEN_TABLE, this::spawnAtLocation);
            } else if (level().isClientSide() && tickCount % 10 == 0) {
                level().addDestroyBlockEffect(pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                level().playLocalSound(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, SoundEvents.NETHERRACK_HIT, getSoundSource(), 1, 1, true);
            }
        } else {
            eatingMagmaProgress = 0;
        }
    }

    public void attackMelee(LivingEntity target) {
        if (!(level() instanceof ServerLevel world)) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        AttributeModifier modifier = new AttributeModifier(UselessReptile.id("magma_cube_bonus"), 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (target instanceof MagmaCube) getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(modifier);
        if (doHurtTarget(world, target)) {
            target.igniteForSeconds((float) (0.75f * getAttributeValue(Attributes.ATTACK_DAMAGE)));
        }
        getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(modifier);
    }

    public BlockPos getMagmaBlockPos() {
        return entityData.get(MAGMA_POS);
    }

    public void setMagmaBlockPos(BlockPos magmaBlockPos) {
        entityData.set(MAGMA_POS, magmaBlockPos);
    }

    @Override
    public boolean canBreakBlocks() {
        if (!(level() instanceof ServerLevel world)) return false;
        boolean shouldBreakBlocks = isTame() ? URConfig.getConfig().magmamuncherGriefing.canTamedBreak() : URConfig.getConfig().magmamuncherGriefing.canUntamedBreak();
        return shouldBreakBlocks &&  world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
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
