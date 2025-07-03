package nordmods.uselessreptile.common.entity;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
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
    public static final float BASE_GROUND_SPEED = 0.3f;
    public MagmamuncherEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        baseTamingProgress = 12;
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
    //TODO: in blockbench - turn, blink and attack animations
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
        event.controller().setAnimationSpeed(animationSpeed);
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
}
