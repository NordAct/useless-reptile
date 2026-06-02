package nordmods.uselessreptile.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.dragon_variant.DragonVariant;
import nordmods.uselessreptile.common.dragon_variant.type.DragonVariantType;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawEscapeLightGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawUntamedTargetGoal;
import nordmods.uselessreptile.common.entity.ai.navigation.MoleclawNavigation;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.event.DragonGetBlockMiningLevelEvent;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URDragonVariantTypes;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.util.URDragonAnimationController;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class Moleclaw extends URRideableDragonEntity {
    public int attackDelay = 0;
    public static final float defaultWidth = 2f;
    public static final float defaultHeight = 2.9f;
    private int panicSoundDelay = 0;

    public static final float BASE_GROUND_SPEED = 0.25f;

    public Moleclaw(EntityType<? extends URRideableDragonEntity> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;
        navigation = new MoleclawNavigation(this, world);

        pitchLimitGround = 50;
        ticksUntilHeal = 400;
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
        targetSelector.addGoal(5, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(6, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(4, new DragonRevengeGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PANICKING, false);
    }
    public static final EntityDataAccessor<Boolean> IS_PANICKING = SynchedEntityData.defineId(Moleclaw.class, EntityDataSerializers.BOOLEAN);
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
                ;
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
        URDragonAnimationController<URDragonEntity> blinkController = getAnimationController(AnimationController.BLINK);
        if (blinkController.getPlayingAnimations().isEmpty()) blinkController.playAnimation("blink");
    }

    private void tickAttackController() {
        URDragonAnimationController<URDragonEntity> attackController = getAnimationController(AnimationController.ATTACK);
        if (isSecondaryAttack()) {
            attackController.getPlayingAnimations().forEach(anim -> anim.setSpeed(1f / getCooldownModifier()));
            attackController.playAnimation("attack.normal" + getAttackType());
            return;
        }
        if (isPrimaryAttack()) {
            if (isPanicking()) {
                attackController.playAnimation("attack.strong.panic");
                return;
            }
            attackController.playAnimation("attack.strong");
        }
    }

    private void tickTurnController() {
        URDragonAnimationController<URDragonEntity> turnController = getAnimationController(AnimationController.TURN);
        switch (getTurningState()) {
            case LEFT -> turnController.playAnimation("turn.left");
            case RIGHT -> turnController.playAnimation("turn.right");
            default -> turnController.getPlayingAnimations().forEach(BRPlayingAnimation::stop);
        }
    }

    private void tickMainController() {
        URDragonAnimationController<URDragonEntity> mainController = getAnimationController(AnimationController.MAIN);
        float animationSpeed = getMovementSpeedModifier();
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(animationSpeed));
        if (isOrderedToSit() && !isDancing() && !isPanicking()) {
            mainController.playAnimation("sit");
            return;
        }
        if (isMoving() || isMoveForwardPressed() || isMovingBackwards()) {
            if (isPanicking()) {
                mainController.playAnimation("panic");
                return;
            }
            mainController.playAnimation("walk");
            return;
        }
        mainController.getPlayingAnimations().forEach(anim -> anim.setSpeed(1));
        if (isDancing() && !isVehicle()) {
            mainController.playAnimation("dance");
            return;
        }
        if (isPanicking()) {
            mainController.playAnimation("panic.idle");
            return;
        }
        mainController.playAnimation("idle");
    }

    @Override
    public void tick() {
        super.tick();
        if (!isOrderedToSit()) setHitboxModifiers(1, 1, 2.5f);
        else setHitboxModifiers(0.75f, 1f, 2.5f);
        tryPanic();

        if (hasControllingPassenger()) {
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
        tickAnimations();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    public void meleeAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> targets = level()
                .getEntities(
                        this,
                        getSecondaryAttackBox(),
                        entity -> !getPassengers().contains(entity)
                                && !entity.is(URTags.DRAGON_IMMUNE)
                                && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
                );
        if (!targets.isEmpty()) for (Entity mob: targets) {
            AABB targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getSecondaryAttackBox())) doHurtTarget(world, mob);
        }
    }

    public void strongAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> targets = level()
                .getEntities(
                        this,
                        getPrimaryAttackBox(),
                        entity -> !getPassengers().contains(entity)
                                && !entity.is(URTags.DRAGON_IMMUNE)
                                && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
                );
        if (!targets.isEmpty()) for (Entity mob : targets) {
            AABB targetBox = mob.getBoundingBox();
            if (targetBox.intersects(getPrimaryAttackBox())) doHurtTarget(world, mob);
        }

        if (!canBreakBlocks()) return;

        Iterable<BlockPos> blocks = BlockPos.betweenClosed(getPrimaryAttackBox());
        float maxMiningLevel = (float) getAttributeValue(URAttributes.DRAGON_MINING_LEVEL);
        if (hasEffect(MobEffects.STRENGTH)) maxMiningLevel += getEffect(MobEffects.STRENGTH).getAmplifier() + 1;
        if (hasEffect(MobEffects.WEAKNESS)) maxMiningLevel -= getEffect(MobEffects.WEAKNESS).getAmplifier() + 1;
        for (BlockPos blockPos : blocks) {
            if (isBlockProtected(blockPos)) continue;

            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock().defaultDestroyTime() < 0) continue;

            float miningLevel = DragonGetBlockMiningLevelEvent.EVENT.invoker().getMiningLevel(blockState);
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
        return shouldBreakBlocks &&  world.getGameRules().get(GameRules.MOB_GRIEFING);
    }

    @Override
    public @NonNull AABB getAttackBoundingBox(double range) {
        return getSecondaryAttackBox();
    }

    @Override
    public @NonNull AABB getSecondaryAttackBox() {
        Vec3 rotationVec = calculateViewVector(0, getYRot());
        double x = rotationVec.x * 2;
        double z = rotationVec.z * 2;
        return new AABB(position().x() + x - 1.5, position().y(), position().z() + z - 1.5,
                position().x() + x + 1.5, position().y() + getBbHeight(), position().z() + z + 1.5);
    }

    @Override
    public @NonNull AABB getPrimaryAttackBox() {
        double halfWidth = getBbWidth() / 2f;
        double x = -Math.sin(Math.toRadians(getYRot())) * halfWidth;
        double y = 0;
        if (hasControllingPassenger()) {
            if (getXRot() > 25) y = -1;
            if (getXRot() < -25) y = 1;
        } else y = -Math.sin(Math.toRadians(getXRot()));
        double z = Math.cos(Math.toRadians(getYRot())) * halfWidth;
        double heightIncrease = hasControllingPassenger() ? 2 : 1;
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
        Level level = entity.level();
        int lightLevelBlock = level.getBrightness(LightLayer.BLOCK, blockPos);
        int lightLevelSky = level.getBrightness(LightLayer.SKY, blockPos);
        return Math.max(lightLevelBlock, level.environmentAttributes().getValue(EnvironmentAttributes.MONSTERS_BURN, entity.position()) ? lightLevelSky : 0);
    }

    @Override
    public double getFluidJumpThreshold() {
        return 1;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        if (isPanicking()) return null;
        return super.getControllingPassenger();
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
    public float getWalkTargetValue(@NonNull BlockPos pos, @NonNull LevelReader world) {
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
    protected boolean canTeleportTo(@NonNull BlockPos pos) {
        if (isTooBrightAtPos(pos)) return false;
        return super.canTeleportTo(pos);
    }

    @Override
    public boolean isLookingAtDirection(float pitch, float yaw, float pitchTolerance, float yawTolerance) {
        return isPanicking() || super.isLookingAtDirection(pitch, yaw, pitchTolerance, yawTolerance);
    }

    @Override
    protected DragonInventory.StorageSize getStorageSize() {
        return DragonInventory.StorageSize.LARGE;
    }

    @Override
    public DragonVariantType<? extends DragonVariant> getVariantType() {
        return URDragonVariantTypes.MOLECLAW;
    }
}
