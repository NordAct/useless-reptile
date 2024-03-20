package nordmods.uselessreptile.common.entity;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.CommonProtection;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.config.URMobAttributesConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawEscapeLightGoal;
import nordmods.uselessreptile.common.entity.ai.goal.moleclaw.MoleclawUntamedTargetGoal;
import nordmods.uselessreptile.common.entity.ai.pathfinding.MoleclawNavigation;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.gui.MoleclawScreenHandler;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class MoleclawEntity extends URRideableDragonEntity {
    public int attackDelay = 0;
    public static final float defaultWidth = 2f;
    public static final float defaultHeight = 2.9f;
    private int attackType = 1;
    private int panicSoundDelay = 0;

    public MoleclawEntity(EntityType<? extends URRideableDragonEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;
        navigation = new MoleclawNavigation(this, world);

        pitchLimitGround = 50;
        rotationSpeedGround = 6;
        basePrimaryAttackCooldown = 60;
        baseSecondaryAttackCooldown = 30;
        baseTamingProgress = 64;
        regenFromFood = 2;
        ticksUntilHeal = 400;
    }

    public static boolean canDragonSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        if (world.getChunk(pos).getInhabitedTime() > 12000) return false;
        if (world.getLightLevel(LightType.SKY, pos) > 7 || world.getLightLevel(LightType.BLOCK, pos) > 7) return false;
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(pos.down()).isIn(URTags.MOLECLAW_SPAWNABLE_ON);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new MoleclawEscapeLightGoal(this));
        goalSelector.add(2, new DragonCallBackGoal(this));
        goalSelector.add(3, new SitGoal(this));
        goalSelector.add(4, new DragonConsumeFoodFromInventoryGoal(this));
        goalSelector.add(8, new MoleclawAttackGoal(this, 512));
        goalSelector.add(9, new DragonWanderAroundGoal(this));
        goalSelector.add(10, new DragonLookAroundGoal(this));
        targetSelector.add(5, new MoleclawUntamedTargetGoal<>(this, PlayerEntity.class));
        targetSelector.add(6, new MoleclawUntamedTargetGoal<>(this, ChickenEntity.class));
        targetSelector.add(5, new DragonAttackWithOwnerGoal<>(this));
        targetSelector.add(6, new DragonTrackOwnerAttackerGoal(this));
        targetSelector.add(4, new DragonRevengeGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(IS_PANICKING, false);
    }
    public static final TrackedData<Boolean> IS_PANICKING = DataTracker.registerData(MoleclawEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public boolean isPanicking() {return dataTracker.get(IS_PANICKING);}
    public void setIsPanicking (boolean state) {dataTracker.set(IS_PANICKING, state);}

    public static DefaultAttributeContainer.Builder createMoleclawAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, getAttributeConfig().moleclawDamage * getAttributeConfig().dragonDamageMultiplier)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, getAttributeConfig().moleclawKnockback * URMobAttributesConfig.getConfig().dragonKnockbackMultiplier)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, getAttributeConfig().moleclawHealth * getAttributeConfig().dragonHealthMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR, getAttributeConfig().moleclawArmor * getAttributeConfig().dragonArmorMultiplier)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, getAttributeConfig().moleclawArmorToughness * getAttributeConfig().dragonArmorToughnessMultiplier)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, getAttributeConfig().moleclawGroundSpeed * getAttributeConfig().dragonGroundSpeedMultiplier)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<MoleclawEntity> main = new AnimationController<>(this, "main", TRANSITION_TICKS, this::main);
        AnimationController<MoleclawEntity> turn = new AnimationController<>(this, "turn", TRANSITION_TICKS, this::turn);
        AnimationController<MoleclawEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<MoleclawEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            if (event.getKeyframeData().getSound().equals("step"))
                playSound(getStepSound(getBlockPos(), getWorld().getBlockState(getBlockPos())), 1, 1);
    }

    private <ENTITY extends GeoEntity> void soundListenerAttack(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "attack_strong" -> playSound(URSounds.MOLECLAW_STRONG_ATTACK, 1, 1F);
                case "attack" -> playSound(URSounds.MOLECLAW_ATTACK, 1, 1);
            }
    }

    private <A extends GeoEntity> PlayState eye(AnimationState<A> event) {
        return loopAnim("blink", event);
    }

    private <A extends GeoEntity> PlayState main(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animationSpeed);
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed() || isMovingBackwards()) {
            if (isPanicking()) return loopAnim("panic", event);
            return loopAnim("walk", event);
        }
        event.getController().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
        if (isPanicking()) return loopAnim("panic.idle", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turn(AnimationState<A> event) {
        byte turnState = getTurningState();
        event.getController().setAnimationSpeed(animationSpeed);
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attack(AnimationState<A> event){
        event.getController().setAnimationSpeed(1/calcCooldownMod());
        if (isSecondaryAttack()) return playAnim( "attack.normal" + attackType, event);
        if (isPrimaryAttack()) {
            if (isPanicking()) return playAnim( "attack.strong.panic", event);
            return playAnim( "attack.strong", event);
        }
        return loopAnim("attack.none", event);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getIsSitting()) setHitboxModifiers(1, 1, 2.5f);
        else setHitboxModifiers(0.75f, 1f, 2.5f);
        tryPanic();

        if (canBeControlledByRider()) {
            if (attackDelay > 0) {
                attackDelay++;
                if (attackDelay > TRANSITION_TICKS + 1) {
                    if (isPrimaryAttack()) strongAttack();
                    if (isSecondaryAttack()) meleeAttack();
                    attackDelay = 0;
                }
            }

            if (isSecondaryAttackPressed) tryNormalAttack();
            if (isPrimaryAttackPressed) tryStrongAttack();
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (!isAlive()) return;

        if (!isMoving()) setSprinting(false);
        if (isSprinting()) setSpeedMod(1.1f);
        else setSpeedMod(1f);
        if (isMovingBackwards()) setSpeedMod(0.6f);
        float speed = (float) getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        setMovementSpeed(speed * getSpeedMod());

        if (canBeControlledByRider()) {
            PlayerEntity rider = (PlayerEntity) getControllingPassenger();

            float f1 = MathHelper.clamp(rider.forwardSpeed, -forwardSpeed, forwardSpeed);

            if (isSprintPressed()) setSprinting(true);
            setMovingBackwards(isMoveBackPressed() || (!isMoveForwardPressed() && !isMoveBackPressed() && isMoving()));
            if (isMovingBackwards()) setSprinting(false);
            setRotation(rider);
            setPitch(MathHelper.clamp(rider.getPitch(), -getPitchLimit(), getPitchLimit()));
            if (isJumpPressed() && isOnGround()) jump();

            super.travel(new Vec3d(0, movementInput.y, f1));
        } else {
            super.travel(movementInput);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
        return MoleclawScreenHandler.createScreenHandler(syncId, inv, inventory);
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

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
            eat(player, hand, itemStack);
            if (random.nextInt(3) == 0) setTamingProgress((byte) (getTamingProgress() - 2));
            else setTamingProgress((byte) (getTamingProgress() - 1));
            if (player.isCreative()) setTamingProgress((byte) 0);
            if (getTamingProgress() <= 0) {
                setOwner(player);
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
            } else {
                getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
            }
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

    public void meleeAttack() {
        List<Entity> targets = getWorld().getOtherEntities(this, getAttackBox(), livingEntity -> !getPassengerList().contains(livingEntity));
        if (!targets.isEmpty()) for (Entity mob: targets) {
            Box targetBox = mob.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(mob);
        }
    }

    public void strongAttack() {
        List<Entity> targets = getWorld().getOtherEntities(this, getSecondaryAttackBox(), livingEntity -> !getPassengerList().contains(livingEntity));
        if (!targets.isEmpty()) for (Entity mob : targets) {
            Box targetBox = mob.getBoundingBox();
            if (doesCollide(targetBox, getSecondaryAttackBox())) tryAttack(mob);
        }

        boolean shouldBreakBlocks = isTamed() ? URConfig.getConfig().allowDragonGriefing.canTamedBreak() : URConfig.getConfig().allowDragonGriefing.canUntamedBreak();
        boolean canBreakBlocks = shouldBreakBlocks && getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        if (getWorld().isClient() || !canBreakBlocks) return;

        Box box = getSecondaryAttackBox();
        Iterable<BlockPos> blocks = BlockPos.iterate((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ);
        for (BlockPos blockPos : blocks) {
            BlockState blockState = getWorld().getBlockState(blockPos);
            PlayerEntity rider = canBeControlledByRider() ? (PlayerEntity) getControllingPassenger() : null;
            GameProfile playerId = rider != null ? rider.getGameProfile() : CommonProtection.UNKNOWN;
            if (blockState.isIn(URTags.DRAGON_UNBREAKABLE) || !CommonProtection.canBreakBlock(getWorld(), blockPos, playerId, rider)) continue;

            float miningLevel = MiningLevelManager.getRequiredMiningLevel(blockState);
            float maxMiningLevel = 0;
            if (hasStatusEffect(StatusEffects.STRENGTH)) maxMiningLevel += getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + 1;
            if (hasStatusEffect(StatusEffects.WEAKNESS)) maxMiningLevel -= getStatusEffect(StatusEffects.WEAKNESS).getAmplifier() + 1;

            if (!blockState.isAir() && miningLevel <= maxMiningLevel) {
                boolean shouldDrop = getRandom().nextDouble() * 100 <= URConfig.getConfig().blockDropChance;
                getWorld().breakBlock(blockPos, shouldDrop, this);
            }
        }
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
        return new Box(getPos().getX() + x - 1.25, getPos().getY() + y + 0.5, getPos().getZ() + z - 1.25,
                getPos().getX() + x + 1.25, getPos().getY() + getHeight() + 1 + y, getPos().getZ() + z + 1.25);
    }

    public void tryPanic() {
        playPanicSound();
        if (!hasHelmet()) setIsPanicking(isTooBrightAtPos(getBlockPos()));
        else setIsPanicking(false);
    }

    public boolean hasHelmet() {
        Item head = getEquippedStack(EquipmentSlot.HEAD).getItem();
        return head == URItems.MOLECLAW_HELMET_IRON || head == URItems.MOLECLAW_HELMET_GOLD || head == URItems.MOLECLAW_HELMET_DIAMOND;
    }

    public boolean isTooBrightAtPos(BlockPos blockPos) {
        return getLightAtPos(blockPos, this) > 7 && !hasHelmet();
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

    public void tryNormalAttack() {
        if (getSecondaryAttackCooldown() == 0) {
            if (attackDelay == 0) attackDelay = 6;
            setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
            attackType = random.nextInt(2)+1;
            if (getWorld() instanceof ServerWorld world)
                for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        }
    }

    public void tryStrongAttack() {
        if (getPrimaryAttackCooldown() == 0) {
            if (attackDelay == 0) attackDelay = 6;
            setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        }
    }

    private SoundEvent getStepSound(BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty()) {
            BlockState blockState = getWorld().getBlockState(pos.up());
            BlockSoundGroup blockSoundGroup = blockState.isIn(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? blockState.getSoundGroup() : state.getSoundGroup();
            return blockSoundGroup.getStepSound();
        }
        return getSwimSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return URSounds.MOLECLAW_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return URSounds.MOLECLAW_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return URSounds.MOLECLAW_AMBIENT;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return -world.getPhototaxisFavor(pos);
    }

    private void playPanicSound() {
        if (isPanicking()) {
            if (panicSoundDelay == 0) {
                playSound(URSounds.MOLECLAW_PANICKING, 1 ,1);
                panicSoundDelay = random.nextInt(41) + 60;
            }
            else panicSoundDelay--;
        } else panicSoundDelay = 2;
    }

    public boolean isFavoriteFood(ItemStack itemStack){
        return itemStack.isOf(Items.BEETROOT);
    }
}
