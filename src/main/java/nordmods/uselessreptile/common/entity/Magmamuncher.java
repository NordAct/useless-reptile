package nordmods.uselessreptile.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
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
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public class Magmamuncher extends URDragonEntity implements HeadMountDragon {
    public static int EAT_MAGMA_COOLDOWN_AVERAGE = 20*50;
    public int eatMagmaCooldown = 0;
    private int eatingMagmaProgress;
    private static final int MAX_EATING_MAGMA_PROGRESS = 20*5;
    public static final float DISTANCE_TO_EAT = 1.25f;
    public static final ResourceKey<LootTable> MAGMA_EATEN_TABLE = ResourceKey.create(Registries.LOOT_TABLE, UselessReptile.id("entities/magmamuncher_from_magma"));
    private final URDragonAnimationController<Magmamuncher> mainController = new URDragonAnimationController<>(this, true);
    private final URDragonAnimationController<Magmamuncher> turnController = new URDragonAnimationController<>(this, true);
    private final URDragonAnimationController<Magmamuncher> attackController = new URDragonAnimationController<>(this, false) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final URDragonAnimationController<Magmamuncher> blinkController = new URDragonAnimationController<>(this, true) {
        @Override
        public float getDefaultTransitionTime() {
            return 0;
        }
    };
    private final List<BRAnimationController> controllers = List.of(mainController, turnController, attackController, blinkController);

    public static final float BASE_GROUND_SPEED = 0.2f;

    public Magmamuncher(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        sprintSpeedModifier = 1.3f;
        setPathfindingMalus(PathType.FIRE, 0);
        setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, 0);
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

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    //todo reconsider structure and make it cleaner
    public void tickAnimations() {
        if (!level().isClientSide()) return;
        tickBlinkController();
        tickTurnController();
        tickAttackController();
        tickMainController();
    }

    private void tickBlinkController() {
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickAttackController() {
        attackController.getPlayingAnimations().forEach(anim -> anim.setSpeed(1f / getCooldownModifier()));
        if (isPrimaryAttack()) attackController.playAnimation("attack" + getAttackType());
    }

    private void tickTurnController() {
        byte turnState = getTurningState();
        if (isMoving()) {
            if (turnState == 1) {
                turnController.playAnimation("turn.walk.left");
                return;
            }
            if (turnState == 2) {
                turnController.playAnimation("turn.walk.right");
                return;
            }
        }
        if (turnState == 1) {
            turnController.playAnimation("turn.left");
            return;
        }
        if (turnState == 2) {
            turnController.playAnimation("turn.right");
            return;
        }
        turnController.getPlayingAnimations().forEach(BRPlayingAnimation::stop);
    }

    private void tickMainController() {
        float animationSpeed = getMovementSpeedModifier();
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(animationSpeed));
        if (isPassenger()) {
            mainController.playAnimation("sit.head");
            return;
        }
        if (isOrderedToSit() && !isDancing()) {
            mainController.playAnimation("sit");
            return;
        }
        if (isMoving()) {
            mainController.playAnimation("walk");
            return;
        }
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(1));
        if (isEatingMagma()) {
            mainController.playAnimation("eat");
            return;
        }
        if (isDancing()) {
            mainController.playAnimation("dance");
            return;
        }
        mainController.playAnimation("idle");
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
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EATING_MAGMA, false);
        builder.define(MAGMA_POS, BlockPos.ZERO);
    }
    public static final EntityDataAccessor<Boolean> EATING_MAGMA = SynchedEntityData.defineId(Magmamuncher.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> MAGMA_POS = SynchedEntityData.defineId(Magmamuncher.class, EntityDataSerializers.BLOCK_POS);
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
        goalSelector.addGoal(7, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(8, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(9, new DragonLookAroundGoal(this));
        targetSelector.addGoal(1, new DragonRevengeGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, MagmaCube.class, true));
    }

    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput tag) {
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
        tickAnimations();
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
                eatMagmaCooldown = Magmamuncher.EAT_MAGMA_COOLDOWN_AVERAGE + getRandom().nextIntBetweenInclusive(-20 * 10, 20 * 10);
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
        return shouldBreakBlocks &&  world.getGameRules().get(GameRules.MOB_GRIEFING);
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.SMALL;
    }
    @Override
    public @NonNull AABB getPrimaryAttackBox() {
        return getBoundingBox().inflate(getScale(), 0, getScale());
    }
}
