package nordmods.uselessreptile.common.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.*;
import nordmods.uselessreptile.common.entity.ai.goal.wyvern.WyvernAttackGoal;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URDragonPart;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.entity.misc.ShootingPoint;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import nordmods.uselessreptile.common.gui.URDragonScreenHandler;
import nordmods.uselessreptile.common.init.*;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import nordmods.uselessreptile.common.network.URPacketHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

public class WyvernEntity extends URRideableFlyingDragonEntity implements MultipartEntity, ShooterDragon {
    private final URDragonPart wingLeft = new URDragonPart(this);
    private final URDragonPart wingRight = new URDragonPart(this);
    private final URDragonPart neck = new URDragonPart(this);
    private final URDragonPart head = new URDragonPart(this);
    private final URDragonPart tail1 = new URDragonPart(this);
    private final URDragonPart tail2 = new URDragonPart(this);
    private final URDragonPart tail3 = new URDragonPart(this);
    private final URDragonPart[] parts = new URDragonPart[]{wingLeft, wingRight, neck, head, tail1, tail2, tail3};
    private ShootingPoint shootingPoint = new ShootingPoint(position(), getLookAngle());

    public static final float BASE_GROUND_SPEED = 0.2f;

    public WyvernEntity(EntityType<? extends URRideableFlyingDragonEntity> entityType, Level world) {
        super(entityType, world);
        xpReward = 20;

        pitchLimitGround = 50;
        pitchLimitAir = 20;
        ticksUntilHeal = 200;
        sprintSpeedModifier = 1.2f;
    }

    @Override
    public void setShootingPoint(ShootingPoint point) {
        //dataTracker.set(SHOOTING_POINT, point);
        shootingPoint = point;
    }

    @Override
    public ShootingPoint getShootingPoint() {
        //return dataTracker.get(SHOOTING_POINT);
        return shootingPoint;
    }

    @Override
    public Vec3 getShootingPointAnchor() {
        return head
                .position()
                .add(0, head.getBbHeight() / 2f, 0);
    }

    @Override
    public float getShootingPointDesiredPitch() {
        return getXRot();
    }

    @Override
    public float getShootingPointDesiredYaw() {
        return getYawWithAdjustment();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new FlyingDragonCallBackGoal<>(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new DragonEatFromInventoryGoal(this));
        goalSelector.addGoal(6, new WyvernAttackGoal(this, 512));
        goalSelector.addGoal(7, new FlyingDragonFlyDownGoal<>(this, 30));
        goalSelector.addGoal(8, new DragonReturnToHomePoint(this));
        goalSelector.addGoal(9, new DragonWanderAroundGoal(this));
        goalSelector.addGoal(9, new FlyingDragonFlyAroundGoal<>(this, 30));
        goalSelector.addGoal(10, new DragonLookAroundGoal(this));
        targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Chicken.class, true, null));
        targetSelector.addGoal(5, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(4, new DragonRevengeGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
    }

    public static AttributeSupplier.Builder createWyvernAttributes() {
        return createDragonAttributes()
                .add(Attributes.ATTACK_DAMAGE, attributes().wyvernDamage)
                .add(Attributes.ATTACK_KNOCKBACK, attributes().wyvernKnockback)
                .add(Attributes.MAX_HEALTH, attributes().wyvernHealth)
                .add(Attributes.ARMOR, attributes().wyvernArmor)
                .add(Attributes.ARMOR_TOUGHNESS, attributes().wyvernArmorToughness)
                .add(Attributes.MOVEMENT_SPEED, attributes().wyvernGroundSpeed)
                .add(Attributes.FLYING_SPEED, attributes().wyvernFlyingSpeed)
                .add(Attributes.JUMP_STRENGTH, 0.42 * 1.5)
                .add(URAttributes.DRAGON_VERTICAL_SPEED, attributes().wyvernVerticalSpeed)
                .add(URAttributes.DRAGON_ACCELERATION_DURATION, attributes().wyvernBaseAccelerationDuration)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().wyvernRotationSpeedGround)
                .add(URAttributes.DRAGON_FLYING_ROTATION_SPEED, attributes().wyvernRotationSpeedAir)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().wyvernBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_SECONDARY_ATTACK_COOLDOWN, attributes().wyvernBaseSecondaryAttackCooldown);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<WyvernEntity> main = new AnimationController<>("main", TRANSITION_TICKS, this::mainController);
        AnimationController<WyvernEntity> turn = new AnimationController<>("turn", TRANSITION_TICKS, this::turnController);
        AnimationController<WyvernEntity> attack = new AnimationController<>("attack", 0, this::attackController);
        AnimationController<WyvernEntity> eye = new AnimationController<>("eye", 0, this::eyeController);
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
        if (event.controller().hasAnimationFinished()) event.controller().forceAnimationReset();
        event.controller().transitionLength((int) (TRANSITION_TICKS / event.controller().getAnimationSpeed()));
        event.controller().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isSecondaryAttack()) {
                event.controller().setAnimationSpeed(1/ getCooldownModifier());
                return loopAnim("fly.attack", event);
            }
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (isFlyGliding()) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.controller().setAnimationSpeed(Math.max(animationSpeed, 1));
            return loopAnim("fly.idle", event);
        }
        if (isOrderedToSit() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving() || isMoveForwardPressed()) return loopAnim("walk", event);
        event.controller().setAnimationSpeed(1);
        if (isDancing() && !isVehicle()) return loopAnim("dance", event);
        return loopAnim("idle", event);
    }

    private <A extends GeoEntity> PlayState turnController(AnimationTest<A> event) {
        byte turnState = getTurningState();
        if (isFlying() && (isMoving() || event.isMoving()) && !isSecondaryAttack() && !isMovingBackwards()) {
            if (turnState == 1) return loopAnim("turn.fly.left", event);
            if (turnState == 2) return loopAnim("turn.fly.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attackController(AnimationTest<A> event) {
        event.controller().setAnimationSpeed(1/ getCooldownModifier());
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + getAttackType(), event);
        if (isPrimaryAttack()) {
            if (isFlying() && (isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.fly.range", event);
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        Holder<MobEffect> type = effect.getEffect();
        return !(type == URStatusEffects.ACID || type == MobEffects.POISON || type == MobEffects.HUNGER);
    }

    @Override
    public void tick() {
        super.tick();

        float dHeight;
        float dWidth;
        float dMountedOffset;
        if (isFlying()) {
            dWidth = 2.95f;
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
            dWidth = 1.8f;
        }
        setHitboxModifiers(dHeight, dWidth, dMountedOffset);

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed() && getSecondaryAttackCooldown() == 0) {
                meleeAttack();
            }
            if (isPrimaryAttackPressed() && getPrimaryAttackCooldown() == 0) shoot();
        }

        updateChildParts();
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isSaddle(ItemStack itemStack) {
        return itemStack.is(URTags.WYVERN_SADDLES);
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
        return new DragonInventory(dragon, DragonInventory.StorageSize.SMALL, false, true, true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (isTame()) {
            if (itemStack.getItem() == Items.GLASS_BOTTLE && isOwnedBy(player)) {
                Item bottle = itemStack.getItem();
                ItemStack potion = new ItemStack(Items.POTION);
                potion.set(DataComponents.POTION_CONTENTS, new PotionContents(URPotions.ACID));
                player.awardStat(Stats.ITEM_USED.get(bottle));
                level().playPlayerSound(SoundEvents.BOTTLE_FILL, player.getSoundSource(), 1.0F, 1.0F);
                consumeGivenItem(player, itemStack, SoundEvents.BOTTLE_FILL, hand);
                player.addItem(potion);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).withEyeHeight(getBbHeight() * 0.95f);
    }

    @Override
    public double getFluidJumpThreshold() {
        return 1;
    }

    public void shoot() {
        if (level().isClientSide()) return;
        setPrimaryAttackCooldown(getMaxPrimaryAttackCooldown());
        for (int i = 0; i < 5; ++i) {
            AcidBlastEntity projectileEntity = new AcidBlastEntity(level(), this);
            projectileEntity.setPos(getShootingPoint().pos());
            Vec3 rot = getShootingPoint().rotation().scale(0.5f);
            projectileEntity.shoot(rot.x, rot.y, rot.z, 3.0f, 5.0f);
            level().addFreshEntity(projectileEntity);
        }
    }

    public float getYawProgressLimit() {
        return 55;
    }

    public void meleeAttack() {
        if (!(level() instanceof ServerLevel world)) return;
        List<Entity> list = world.getEntities(
                this,
                getAttackBoundingBox(),
                entity -> !getPassengers().contains(entity)
                        && !entity.is(this)
                        && (entity instanceof LivingEntity livingEntity && canAttack(livingEntity) || !(entity instanceof LivingEntity))
                        && !entity.getType().is(URTags.DRAGON_IMMUNE));
        Entity target = null;
        if (!list.isEmpty()) {
            target = list.getFirst();
            for (Entity entry : list) {
                if (distanceToSqr(entry) < distanceToSqr(target)) target = entry;
            }
        }
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        setAttackType(random.nextInt(3)+1);
        if (isFlying()) {
            SoundInfo soundInfo = getSoundInfo("bite");
            if (soundInfo != null)
                URPacketHelper.playSound(this, SoundEvent.createVariableRangeEvent(soundInfo.id()), getSoundSource(), soundInfo.volume(), getRandom().triangle(soundInfo.pitch(), soundInfo.pitchDeviation()), 3);
        }
        if (target != null && !getPassengers().contains(target)) {
            AABB targetBox = target.getBoundingBox();
            if (targetBox.intersects(getAttackBoundingBox())) doHurtTarget(world, target);
        }
    }

    @Override
    public AABB getAttackBoundingBox() {
        double modifier = isFlying() ? getWidthMod() / 2 : (getWidthMod() + 0.1);
        double x = -Math.sin(Math.toRadians(getYRot())) * modifier;
        double z = Math.cos(Math.toRadians(getYRot())) * modifier;
        double y = isFlying() ? -2 : 0;
        return new AABB(position().x() + x - getWidthMod() / 1.5, position().y() + y, position().z() + z - getWidthMod() / 1.5,
                position().x() + x + getWidthMod() / 1.5, position().y() + getBbHeight() + 1, position().z() + z + getWidthMod() / 1.5);
    }

    @Override
    public String getDefaultVariant() {
        return "green";
    }

    @Override
    public float getHeightModTransSpeed() {
        return (float) (0.13 * getScale());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (!level().isClientSide()) GUIEntityToRenderS2CPacket.send((ServerPlayer) player, this);
        return new URDragonScreenHandler(URScreenHandlers.WYVERN_INVENTORY, syncId, inv, getInventory());
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return URConfig.getConfig().wyvernMaxGroupSize * 2;
    }

    @Override
    public EntityPart[] getParts() {
        return parts;
    }

    public void updateChildParts() {
        Vec2 wingLeftScale;
        Vec2 wingRightScale;

        Vector3f wingLeftPos;
        Vector3f wingRightPos;
        Vector3f neckPos;
        Vector3f headPos;
        Vector3f tail1Pos;
        Vector3f tail2Pos;
        Vector3f tail3Pos;

        float yawOffset = getNormalizedRotationProgress();
        float pitchOffset = tiltProgress / TRANSITION_TICKS;

        if (isFlying()) {
            if (isMoving() && !isMovingBackwards() && !isSecondaryAttack()) {
                if (getTiltState() == 2) {
                    wingLeftPos = new Vector3f(2, 0, -0.5f);
                    wingLeftScale = new Vec2(1, 1.5f);

                    wingRightPos = new Vector3f(-2, 0, -0.5f);
                    wingRightScale = new Vec2(1, 1.5f);
                } else {
                    wingLeftPos = new Vector3f(2.5f, 0, -0.5f);
                    wingLeftScale = new Vec2(1, 2.5f);

                    wingRightPos = new Vector3f(-2.5f, 0, -0.5f);
                    wingRightScale = new Vec2(1, 2.5f);
                }
                neckPos = new Vector3f(yawOffset * 0.5f, pitchOffset * 1, 1.75f);
                headPos = new Vector3f(yawOffset * 1.25f, pitchOffset * 1.5f, 2.75f - Math.abs(yawOffset) * 0.5f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, -pitchOffset * 0.25f, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, -pitchOffset * 0.625f, -3 + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, -pitchOffset * 1 , -4 + Math.abs(yawOffset) * 1);
            } else {
                wingLeftPos = new Vector3f(3, 0, -0.5f);
                wingLeftScale = new Vec2(3, 3);

                wingRightPos = new Vector3f(-3, 0, -0.5f);
                wingRightScale = new Vec2(3, 3);

                neckPos = new Vector3f(0, 3, 1);
                headPos = new Vector3f(yawOffset, 3.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.5f, 1, -2);
                tail2Pos = new Vector3f(yawOffset * 1.25f, 0.5f, -2.6f + Math.abs(yawOffset) * 0.5f);
                tail3Pos = new Vector3f(yawOffset * 2f, -0.2f , -3.2f + Math.abs(yawOffset) * 1);
            }
        } else {
            if (isOrderedToSit()) {
                wingLeftPos = new Vector3f(1.3333334f, 0, 0);
                wingLeftScale = new Vec2(1.5f, 2);

                wingRightPos = new Vector3f(-1.3333334f, 0, 0);
                wingRightScale = new Vec2(1.5f, 2);

                neckPos = new Vector3f(0,  2.75f, 0.5f);
                headPos = new Vector3f(0, 3, 1f);
                tail1Pos = new Vector3f(0, 0.3f, -1.6f);
                tail2Pos = new Vector3f(0, 0.2f, -2.6f);
                tail3Pos = new Vector3f(0, 0.1f , -3.6f);

            } else {
                wingLeftPos = new Vector3f(1, 0.5f, 0);
                wingLeftScale = new Vec2(2, 1.5f);

                wingRightPos = new Vector3f(-1, 0.5f, 0);
                wingRightScale = new Vec2(2, 1.5f);

                neckPos = new Vector3f(0, 3, 1);
                headPos = new Vector3f(yawOffset, 3.1f, 1.9f);
                tail1Pos = new Vector3f(yawOffset * 0.25f, 1.5f, -1.6f);
                tail2Pos = new Vector3f(yawOffset * 0.75f, 1.0f, -2.6f);
                tail3Pos = new Vector3f(yawOffset * 1.45f, 0.25f, -3.2f);
            }
        }

        wingLeft.setRelativePos(wingLeftPos);
        wingLeft.setScale(wingLeftScale);

        wingRight.setRelativePos(wingRightPos);
        wingRight.setScale(wingRightScale);

        head.setRelativePos(headPos);
        head.setScale(1 ,1);

        neck.setRelativePos(neckPos);
        neck.setScale(1 ,1);

        tail1.setRelativePos(tail1Pos);
        tail1.setScale(1 ,1);

        tail2.setRelativePos(tail2Pos);
        tail2.setScale(1 ,1);

        tail3.setRelativePos(tail3Pos);
        tail3.setScale(1 ,1);
    }
}
