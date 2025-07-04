package nordmods.uselessreptile.common.entity;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonCallBackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonLookAroundGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonRevengeGoal;
import nordmods.uselessreptile.common.entity.ai.goal.common.DragonWanderAroundGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherAttackGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherConsumeFoodFromInventoryGoal;
import nordmods.uselessreptile.common.entity.ai.goal.magmamuncher.MagmamuncherEatMagmaGoal;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.misc.DragonInventory;
import nordmods.uselessreptile.common.gui.MagmamuncherScreenHandler;
import nordmods.uselessreptile.common.init.URAttributes;
import nordmods.uselessreptile.common.init.URTags;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;

public class MagmamuncherEntity extends URDragonEntity implements HeadMountDragon {
    public static final float BASE_GROUND_SPEED = 0.26f;

    public MagmamuncherEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        baseTamingProgress = 12;
        sprintSpeedModifier = 1.3f;
        inventory = new DragonInventory(DragonInventory.StorageSize.SMALL, false, false, false);
    }

    @Override
    protected float getBaseGroundSpeed() {
        return BASE_GROUND_SPEED;
    }

    @Override
    public boolean isFavoriteFood(ItemStack itemStack) {
        return getWorld().getFuelRegistry().isFuel(itemStack);
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
        if (!getWorld().isClient()) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            return MagmamuncherScreenHandler.createScreenHandler(syncId, inv, inventory);
        }
        return null;
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
        event.controller().setAnimationSpeed(animationSpeed);
        if (hasVehicle()) return loopAnim("sit.head", event);
        if (getIsSitting() && !isDancing()) return loopAnim("sit", event);
        if (event.isMoving()) return loopAnim("walk", event);
        event.controller().setAnimationSpeed(1);
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
                .add(EntityAttributes.MOVEMENT_SPEED, attributes().magmamuncherGroundSpeed * attributes().dragonGroundSpeedMultiplier)
                .add(URAttributes.DRAGON_GROUND_ROTATION_SPEED, attributes().magmamuncherRotationSpeedGround)
                .add(URAttributes.DRAGON_PRIMARY_ATTACK_COOLDOWN, attributes().magmamuncherBasePrimaryAttackCooldown)
                .add(URAttributes.DRAGON_REGENERATION_FROM_FOOD, attributes().magmamuncherRegenerationFromFood);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(1, new DragonCallBackGoal(this));
        goalSelector.add(2, new SitGoal(this));
        goalSelector.add(3, new MagmamuncherConsumeFoodFromInventoryGoal(this));
        goalSelector.add(5, new MagmamuncherAttackGoal(this, 4096));
        goalSelector.add(6, new MagmamuncherEatMagmaGoal());
        goalSelector.add(8, new DragonWanderAroundGoal(this));
        goalSelector.add(9, new DragonLookAroundGoal(this));
        targetSelector.add(1, new DragonRevengeGoal(this));
        targetSelector.add(2, new AttackWithOwnerGoal(this));
        targetSelector.add(3, new TrackOwnerAttackerGoal(this));
        if (URConfig.getConfig().dragonMadness) targetSelector.add(4, new UntamedActiveTargetGoal<>(this, PlayerEntity.class, true, null));
        targetSelector.add(5, new ActiveTargetGoal<>(this, MagmaCubeEntity.class, true, null));
    }

    @Override
    public void tick() {
        super.tick();
        if (getVehicle() instanceof PlayerEntity) setHitboxModifiers(0.35f, 0.6f, 0);
        else setHitboxModifiers(0.35f, 0.7f, 0);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (isTamingItem(itemStack) && !isTamed()) {
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
}
