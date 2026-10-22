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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherApplyFireResistanceGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherEatMagmaGoal;
import nordmods.uselessreptile.common.entity.ai.navigation.MagmamuncherNavigation;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.init.URBlocks;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;

public class Magmamuncher extends URDragonEntity implements HeadMountDragon {
    public static final int EAT_MAGMA_COOLDOWN_AVERAGE = 20*50;
    public int eatMagmaCooldown = 0;
    private int eatingMagmaProgress;
    private static final int MAX_EATING_MAGMA_PROGRESS = 20*5;
    public static final float DISTANCE_TO_EAT = 1.25f;
    public static final float DISTANCE_TO_EAT_SQUARED = DISTANCE_TO_EAT * DISTANCE_TO_EAT;
    public static final ResourceKey<LootTable> MAGMA_EATEN_TABLE = ResourceKey.create(Registries.LOOT_TABLE, UselessReptile.id("entities/magmamuncher_from_magma"));

    public Magmamuncher(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        sprintSpeedModifier = 1.3f;
        setPathfindingMalus(PathType.FIRE, 0);
        setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, 0);
        navigation = new MagmamuncherNavigation(this, level());
        setYawTargetLimit(20);
        setPitchTargetLimit(20);
    }

    @Override
    public String getDefaultVariant() {
        return "netherrack";
    }

    public void tickAnimations() {
        if (!level().isClientSide()) return;
        tickBlinkController();
        tickMainController();
    }

    private void tickBlinkController() {
        URDragonAnimationController<URDragonEntity> blinkController = getAnimationController(AnimationController.BLINK);
        if (blinkController.isPlayingAbilityAnimation(AnimationController.BLINK)) return;
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickMainController() {
        URDragonAnimationController<URDragonEntity> mainController = getAnimationController(AnimationController.MAIN);
        float animationSpeed = getMovementSpeedModifier();
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(animationSpeed));
        if (mainController.isPlayingAbilityAnimation(AnimationController.MAIN)) return;
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
        goalSelector.addGoal(1, new DragonCallBackGoal<>(this));
        goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(3, new MagmamuncherApplyFireResistanceGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal<>(this));
        goalSelector.addGoal(5, new DragonAttackGoal(this));
        goalSelector.addGoal(6, new MagmamuncherEatMagmaGoal(this));
        goalSelector.addGoal(7, new DragonReturnToHomePoint<>(this));
        goalSelector.addGoal(8, new DragonWanderAroundGoal<>(this));
        goalSelector.addGoal(9, new DragonLookAroundGoal<>(this));
        targetSelector.addGoal(1, new DragonRevengeGoal<>(this));
        targetSelector.addGoal(2, new DragonOwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DragonOwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new DragonNonTameRandomTargetGoal(this, true));
        targetSelector.addGoal(4, new DragonTameRandomTargetGoal(this, true));
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
        if (eatMagmaCooldown > 0) eatMagmaCooldown--;
        checkIfEatingMagma();
        tickAnimations();
    }

    private void checkIfEatingMagma() {
        if (isEatingMagma()) {
            BlockPos pos = getMagmaBlockPos();
            if (!level().isClientSide() &&
                    (level().getBlockState(pos).getBlock() != Blocks.MAGMA_BLOCK
                            || pos.getCenter().distanceToSqr(position()) >= DISTANCE_TO_EAT_SQUARED)) {
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

    public BlockPos getMagmaBlockPos() {
        return entityData.get(MAGMA_POS);
    }

    public void setMagmaBlockPos(BlockPos magmaBlockPos) {
        entityData.set(MAGMA_POS, magmaBlockPos);
    }

    @Override
    public boolean canGrief() {
        if (!super.canGrief()) return false;
        return isTame() ? URConfig.getConfig().magmamuncherGriefing.canTamedGrief() : URConfig.getConfig().magmamuncherGriefing.canUntamedGiref();
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.SMALL;
    }

    @Override
    public DragonVariantType<? extends DragonVariant> getVariantType() {
        return URDragonVariantTypes.MAGMAMUNCHER;
    }
}
