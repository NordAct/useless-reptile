package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.ai.goal.swamp_wyvern.WyvernAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.gui.WyvernScreenHandler;
import nordmods.uselessreptile.common.init.URConfig;
import nordmods.uselessreptile.common.init.URPotions;
import nordmods.uselessreptile.common.init.URSounds;
import nordmods.uselessreptile.common.init.URStatusEffects;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;
import nordmods.uselessreptile.common.network.URPacketManager;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

public class WyvernEntity extends URRideableFlyingDragonEntity {
    private int ticksUntilHeal = 400;
    private int glideTimer = 100;
    private boolean shouldGlide;

    public WyvernEntity(EntityType<? extends URRideableFlyingDragonEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        baseSecondaryAttackCooldown = 30;
        basePrimaryAttackCooldown = 80;
        baseAccelerationDuration = 400;
        baseTamingProgress = 128;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        rotationSpeedGround = 8;
        rotationSpeedAir = 4;
        favoriteFood = Items.CHICKEN;
        regenFromFood = 4;
        dragonID = "wyvern";
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new SwimGoal(this));
        goalSelector.add(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.add(3, new SitGoal(this));
        goalSelector.add(4, new DragonConsumeFoodFromInventoryGoal(this));
        goalSelector.add(6, new WyvernAttackGoal(this, 512));
        goalSelector.add(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.add(8, new DragonWanderAroundGoal(this));
        goalSelector.add(8, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.add(9, new DragonLookAroundGoal(this));
        targetSelector.add(5, new UntamedActiveTargetGoal<>(this, ChickenEntity.class, true, null));
        targetSelector.add(5, new DragonAttackWithOwnerGoal<>(this));
        targetSelector.add(4, new DragonRevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createWyvernAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0 * URConfig.getHealthMultiplier())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.7)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0 * URConfig.getDamageMultiplier())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_ARMOR, 4);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<WyvernEntity> main = new AnimationController<>(this, "main", transitionTicks, this::main);
        AnimationController<WyvernEntity> turn = new AnimationController<>(this, "turn", transitionTicks, this::turn);
        AnimationController<WyvernEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<WyvernEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "flap" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 3, 0.7F);
                case "woosh" -> playSound(URSounds.DRAGON_WOOSH, 2, 1);
                case "step" -> playSound(URSounds.WYVERN_STEP, 1, 1);
            }
    }

    private <ENTITY extends GeoEntity> void soundListenerAttack(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "shoot" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, 2, 1);
                case "bite" ->  playSound(URSounds.WYVERN_BITE, 1, 1);
            }
    }

    private <A extends GeoEntity> PlayState eye(AnimationState<A> event) {
        return loopAnim("blink", event);
    }
    private <A extends GeoEntity> PlayState main(AnimationState<A> event) {
        event.getController().setAnimationSpeed(animSpeed);
        if (isFlying()) {
            if (isSecondaryAttack()) {
                event.getController().setAnimationSpeed(calcCooldownMod());
                return loopAnim("fly.attack", event);
            }
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (isGliding() || shouldGlide) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f && !isClientSpectator()) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.getController().setAnimationSpeed(Math.max(animSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.getController().setAnimationSpeed(1);
        if (isDancing() && !hasPassengers()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turn(AnimationState<A> event) {
        byte turnState = getTurningState();
        event.getController().setAnimationSpeed(animSpeed);
        if (isFlying() && (isMoving() || event.isMoving()) && !isSecondaryAttack() && !isMovingBackwards()) {
            if (turnState == 1) return loopAnim("turn.fly.left", event);
            if (turnState == 2) return loopAnim("turn.fly.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attack(AnimationState<A> event) {
        event.getController().setAnimationSpeed(calcCooldownMod());
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + attackType, event);
        if (isPrimaryAttack()) {
            if (isFlying() && (isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.fly.range", event);
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return URSounds.WYVERN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return URSounds.WYVERN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return URSounds.WYVERN_DEATH;
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        StatusEffect type = effect.getEffectType();
        return !(type == URStatusEffects.ACID || type == StatusEffects.POISON);
    }

    @Override
    public void tick() {
        super.tick();

        float dHeight;
        float dWidth;
        float dMountedOffset;
        if (isFlying()) {
            dWidth = 4f;
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                dHeight = 1f;
                dMountedOffset = 0.6f;
            } else {
                dHeight = 2.95f;
                dMountedOffset = 2.1f;
            }
        } else {
            dHeight = 2.95f;
            dMountedOffset = 2.1f;
            dWidth = 1.8f;
        }
        setHitboxModifiers(dHeight, dWidth, dMountedOffset);

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed && getSecondaryAttackCooldown() == 0) {
                LivingEntity target = getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, getX(), getY(), getZ(), getAttackBox());
                meleeAttack(target);
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) shoot();
        }

        if (ticksUntilHeal <= 0){
            heal(1);
            ticksUntilHeal = 400;
        } else ticksUntilHeal--;

        if (getWorld().isClient()) {
            glideTimer--;
            shouldGlide = glideTimer < 0 && getAccelerationDuration()/getMaxAccelerationDuration() > 0.9;
            if (glideTimer < -50 - getRandom().nextInt(100)) glideTimer = 100 + getRandom().nextInt(100);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isFavoriteFood(itemStack) && !isTamed()) {
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

            if (itemStack.getItem() == Items.GLASS_BOTTLE && isOwnerOrCreative(player)) {
                Item bottle = itemStack.getItem();
                player.incrementStat(Stats.USED.getOrCreateStat(bottle));
                getWorld().playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                ItemUsage.exchangeStack(itemStack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), URPotions.ACID));
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.95f;
    }

    @Override
    public double getSwimHeight() {
        return 1;
    }

    public void shoot() {
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        float progress = rotationProgress / transitionTicks;
        float yaw = 0;
        boolean check = isFlying() && isMoving() && !isMovingBackwards();
        if (getTurningState() != 0 && canBeControlledByRider()) {
            yaw = check ? 90 : 45;
            if (getTurningState() == 1) yaw *= -progress;
            if (getTurningState() == 2) yaw *= progress;
        }
        Vec3d vec3d = getRotationVec(1);
        double y = check ? vec3d.y : 2.5;
        double offset = getTurningState() == 0 ? 4.1 : 1.5;
        for (int i = 0; i < 5; ++i) {
            WyvernProjectileEntity projectileEntity = new WyvernProjectileEntity(getWorld(), this);
            projectileEntity.setPosition(getX() + vec3d.x * offset, getY() + y, getZ() + vec3d.z * offset);
            projectileEntity.setVelocity(this, getPitch(), getYaw() + yaw, 0.5f, 3.0f, 5.0f);
            getWorld().spawnEntity(projectileEntity);
        }
    }

    public void meleeAttack(LivingEntity target) {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        attackType = random.nextInt(3)+1;
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        if (isFlying()) URPacketManager.playSound(this, URSounds.WYVERN_BITE, SoundCategory.HOSTILE, 1, 1, 3);
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(target);
        }
    }

    public Box getAttackBox() {
        //сильно циферки не твикать, расчет положения хитбокса для ближней атаки

        Vec3d rotationVec = getRotationVec(1f);
        double modifier = isFlying() ? 1.5 : 3.5;
        double x = rotationVec.x * modifier;
        double z = rotationVec.z * modifier;
        double y = isFlying() ? -2 : 0;
        double yTop = isMoving() && !isMovingBackwards() ? 2 : 0;
        return new Box(getBlockPos().getX() + x - 2, getBlockPos().getY() + y, getBlockPos().getZ() + z - 2,
                getBlockPos().getX() + x + 2, getBlockPos().getY() + getHeight() + yTop, getBlockPos().getZ() + z + 2);
    }

    @Override
    public float getJumpBoostVelocityModifier() {
        float jumpBoost = hasStatusEffect(StatusEffects.JUMP_BOOST) ? (0.1F * (float) (getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1)) : 0.0f;
        return 0.2f + jumpBoost;
    }

    @Override
    protected float getJumpVelocity() {
        if (hasControllingPassenger()) super.getJumpVelocity();
        return 0.42F * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier()/2;
    }

    @Override
    protected float getHeightModTransSpeed() {
        return (float) (0.13 * animSpeed);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return WyvernScreenHandler.createScreenHandler(syncId, inv, inventory);
    }

    @Override
    protected void updateEquipment() {
        updateSaddle();
        updateBanner();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!getWorld().isClient() && canBeControlledByRider() && isOwnerOrCreative(player)) {
            player.openHandledScreen(this);
        }
    }
}
