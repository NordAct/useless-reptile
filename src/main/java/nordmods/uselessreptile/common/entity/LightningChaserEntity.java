package nordmods.uselessreptile.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import nordmods.uselessreptile.common.init.URSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;

/*
TODO:
    1) Седло
    2) Анимации атак и сами атаки (земля: укус и дыхание, воздух: шоковая волна и дыхание)
    3) Шоковая волна - отражает все снаряды (мб предотвращает обновления всех редстоун компонентов)
    4) Спавн во время шторма (появление в небе)
    5) Механика вызова на бой (ебнуть по мобу с трезубца с каналом)
    6) Приручение по ударам трезубца (точнее его молний)
    7) Звуки
    8) Перетащить систему вариантов из IoB Variant Loader
    9) Возможность переключать управление поворотом дракона на полностью через камеру, частично через камеру и полностью через клавиатуру
    10) Возможность настроить оффсеты камеры для каждого вида драконов отдельно
    11) Заменить dragonID на метод, который бы брал ID из идентификатора (+)
    12) Дыхание - атака лучом буквально, который заканчивается на определенном расстоянии
    14) Вынести мультипарт отдельно от мода
*/

public class LightningChaserEntity extends URRideableFlyingDragonEntity {
    public LightningChaserEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        experiencePoints = 20;

        //baseSecondaryAttackCooldown = 30;
        //basePrimaryAttackCooldown = 80;
        baseAccelerationDuration = 600;
        baseTamingProgress = 3;
        pitchLimitGround = 50;
        pitchLimitAir = 20;
        rotationSpeedGround = 6;
        rotationSpeedAir = 3;
        favoriteFood = Items.CHICKEN;
        regenFromFood = 4;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
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

    public static DefaultAttributeContainer.Builder createLightningChaserAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0 * URConfig.getHealthMultiplier())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.7)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0 * URConfig.getDamageMultiplier())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.3)
                .add(EntityAttributes.GENERIC_ARMOR, 6);
    }
}
