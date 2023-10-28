package nordmods.uselessreptile.common.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import nordmods.uselessreptile.common.gui.LightningChaserScreenHandler;
import nordmods.uselessreptile.common.init.URConfig;
import nordmods.uselessreptile.common.init.UREntities;
import nordmods.uselessreptile.common.init.URSounds;
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

import java.util.Objects;

/*
TODO:
    Дракон:
    1) Анимации атак и сами атаки (земля: укус и дыхание, воздух: шоковая волна и дыхание)
    2) Шоковая волна - отражает все снаряды (мб предотвращает обновления всех редстоун компонентов) / отталкивает от себя ВСЕХ сущностей и накладывает эффект (сделать кастомный эффект шока с дебафами на скорость атаки и перемещения)
    3) Спавн во время шторма (появление в небе)
    4) Механика вызова на бой (ебнуть по мобу с трезубца с каналом)
    5) Приручение по ударам трезубца (точнее его молний)
    6) Звуки
    7) Дыхание - атака лучом буквально, который заканчивается на определенном расстоянии. При ударе об блоки они разлетаются
    ---------------------
    ---------------------
    Прочее:
    1) Возможность переключать управление поворотом дракона на полностью через камеру, частично через камеру и полностью через клавиатуру
    2) Возможность настроить оффсеты камеры для каждого вида драконов отдельно
    3) Вынести мультипарт отдельно от мода
*/

public class LightningChaserEntity extends URRideableFlyingDragonEntity {
    public LightningChaserEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        baseSecondaryAttackCooldown = 30;
        basePrimaryAttackCooldown = 30;
        baseAccelerationDuration = 800;
        baseTamingProgress = 3;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        rotationSpeedGround = 6;
        rotationSpeedAir = 3;
        verticalSpeed = 0.4f;
        //favoriteFood = Items.CHICKEN;
        regenFromFood = 4;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (!getWorld().isClient()) {
            GUIEntityToRenderS2CPacket.send((ServerPlayerEntity) player, this);
            return LightningChaserScreenHandler.createScreenHandler(syncId, inv, inventory);
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        AnimationController<LightningChaserEntity> main = new AnimationController<>(this, "main", transitionTicks, this::main);
        AnimationController<LightningChaserEntity> turn = new AnimationController<>(this, "turn", transitionTicks, this::turn);
        AnimationController<LightningChaserEntity> attack = new AnimationController<>(this, "attack", 0, this::attack);
        AnimationController<LightningChaserEntity> eye = new AnimationController<>(this, "eye", 0, this::eye);
        main.setSoundKeyframeHandler(this::soundListenerMain);
        attack.setSoundKeyframeHandler(this::soundListenerAttack);
        animationData.add(main, turn, attack, eye);
    }

    private <ENTITY extends GeoEntity> void soundListenerMain(SoundKeyframeEvent<ENTITY> event) {
        if (getWorld().isClient())
            switch (event.getKeyframeData().getSound()) {
                case "flap" -> playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 3, 0.6F);
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
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if (isMoving() || event.isMoving()) {
                if (isMovingBackwards()) return loopAnim("fly.back", event);
                if (getTiltState() == 1) return loopAnim("fly.straight.up", event);
                if (getTiltState() == 2) return loopAnim("fly.straight.down", event);
                if (isGliding() || shouldGlide) return loopAnim("fly.straight.glide", event);
                if ((float)getAccelerationDuration()/getMaxAccelerationDuration() < 0.9f && !isClientSpectator()) return loopAnim("fly.straight.heavy", event);
                return loopAnim("fly.straight", event);
            }
            event.getController().setAnimationSpeed(Math.max(animationSpeed, 1));
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
        event.getController().setAnimationSpeed(animationSpeed);
        if (isFlying()) {
            if ((isMoving() || event.isMoving()) && !isMovingBackwards()) {
                if (turnState == 1) return loopAnim("turn.fly.left", event);
                if (turnState == 2) return loopAnim("turn.fly.right", event);
            }
            if (turnState == 1) return loopAnim("turn.fly.idle.left", event);
            if (turnState == 2) return loopAnim("turn.fly.idle.right", event);
        }
        if (turnState == 1) return loopAnim("turn.left", event);
        if (turnState == 2) return loopAnim("turn.right", event);
        return loopAnim("turn.none", event);
    }

    private <A extends GeoEntity> PlayState attack(AnimationState<A> event) {
        event.getController().setAnimationSpeed(calcCooldownMod());
        if (!isFlying() && isSecondaryAttack()) return playAnim( "attack.melee" + attackType, event);
        if (isPrimaryAttack()) {
            if (isFlying()) {
                if ((isMoving() || event.isMoving()) && !isMovingBackwards()) return playAnim("attack.fly.range", event);
                return playAnim("attack.fly.idle.range", event);
            }
            return playAnim("attack.range", event);
        }
        return playAnim("attack.none", event);
    }

    public static DefaultAttributeContainer.Builder createLightningChaserAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0 * URConfig.getHealthMultiplier())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0 * URConfig.getDamageMultiplier())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_ARMOR, 6);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (Objects.equals(damageSource.getTypeRegistryEntry(), DamageTypes.LIGHTNING_BOLT)) return true;
        else return super.isInvulnerableTo(damageSource);
    }

    @Override
    public void tick() {
        super.tick();

        float dHeight;
        float dWidth;
        float dMountedOffset;
        dWidth = 2.95f;
        if (isFlying()) {
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
        }
        setHitboxModifiers(dHeight, dWidth, dMountedOffset);

        if (canBeControlledByRider()) {
            if (isSecondaryAttackPressed && getSecondaryAttackCooldown() == 0) {
                if (isFlying()) shockwave();
                else {
                    LivingEntity target = getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, getX(), getY(), getZ(), getAttackBox());
                    meleeAttack(target);
                }
            }
            if (isPrimaryAttackPressed && getPrimaryAttackCooldown() == 0) shoot();
        }
        //todo бафы во время шторма
        if (getWorld().getLevelProperties().isThundering()) {

        }
    }
    //todo
    public void shoot() {
    }

    public void shockwave() {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        ShockwaveSphereEntity shockwaveSphereEntity =
                new ShockwaveSphereEntity(UREntities.SHOCKWAVE_SPHERE_ENTITY, getWorld());
        shockwaveSphereEntity.setOwner(this);
        shockwaveSphereEntity.setPosition(getPos().add(0, 2.95f, 0));
        shockwaveSphereEntity.setVelocity(Vec3d.ZERO);
        shockwaveSphereEntity.setNoGravity(true);
        getWorld().spawnEntity(shockwaveSphereEntity);
    }

    //todo
    public void meleeAttack(LivingEntity target) {
        setSecondaryAttackCooldown(getMaxSecondaryAttackCooldown());
        attackType = random.nextInt(3)+1;
        if (getWorld() instanceof ServerWorld world)
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, getBlockPos())) AttackTypeSyncS2CPacket.send(player, this);
        if (target != null && !getPassengerList().contains(target)) {
            Box targetBox = target.getBoundingBox();
            if (doesCollide(targetBox, getAttackBox())) tryAttack(target);
        }
    }

    @Override
    public int getMaxSecondaryAttackCooldown() {
        return (int) (isFlying() ? baseSecondaryAttackCooldown * calcCooldownMod()  : baseSecondaryAttackCooldown * calcCooldownMod());
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
        }
        return super.interactMob(player, hand);
    }
}
