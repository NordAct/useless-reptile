package nordmods.uselessreptile.common.entity.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.util.URDragonAnimationController;

import java.util.List;
import java.util.Optional;

public record CommonDragonAbilityData(
        float cooldownTimeSeconds,
        boolean blockOtherAbilitiesIfActive,
        List<ConditionedAnimation> animations,
        UseConditions conditions
) {
    public static final MapCodec<CommonDragonAbilityData> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("cooldown_time_seconds").forGetter(CommonDragonAbilityData::cooldownTimeSeconds),
            Codec.BOOL.fieldOf("block_other_abilities_if_active").forGetter(CommonDragonAbilityData::blockOtherAbilitiesIfActive),
            ConditionedAnimation.CODEC.listOf().fieldOf("animations").forGetter(CommonDragonAbilityData::animations),
            UseConditions.CODEC.fieldOf("conditions").forGetter(CommonDragonAbilityData::conditions)
    ).apply(i, CommonDragonAbilityData::new));

    public record ConditionedAnimation(
            URDragonEntity.AnimationController controller,
            String animationName,
            boolean canInterruptPlayingAbilityAnimation,
            UseConditions conditions
    ) {
        public static final Codec<ConditionedAnimation> CODEC = RecordCodecBuilder.create(i -> i.group(
                StringRepresentable.fromEnum(URDragonEntity.AnimationController::values).fieldOf("controller").forGetter(ConditionedAnimation::controller),
                Codec.STRING.fieldOf("animation_name").forGetter(ConditionedAnimation::animationName),
                Codec.BOOL.fieldOf("can_interrupt_playing_ability_animation").forGetter(ConditionedAnimation::canInterruptPlayingAbilityAnimation),
                UseConditions.CODEC.fieldOf("conditions").forGetter(ConditionedAnimation::conditions)
        ).apply(i, ConditionedAnimation::new));

        public boolean tryPlay(URDragonEntity entity) {
            if (!canInterruptPlayingAbilityAnimation) {
                URDragonAnimationController<?> animationController = entity.getAnimationController(controller);
                for (DragonAbility ability : entity.getAbilityStorage().values()) {
                    if (ability.getCommonAbilityData().animations().isEmpty()) continue;
                    if (!ability.isActive(entity)) continue;
                    for (ConditionedAnimation animation : ability.getCommonAbilityData().animations()) {
                        if (animation.controller != controller) continue;
                        BRPlayingAnimation playingAnimation = animationController.getAnimation(animation.animationName);
                        if (playingAnimation != null && !playingAnimation.isTransitioningOut()) return false;
                    }
                }
            }
            if (!conditions.test(entity)) return false;
            entity.getAnimationController(controller).playAnimation(animationName);
            return true;
        }
    }

    public record UseConditions(
            // common dragon conditions
            Optional<Boolean> moving,
            Optional<Boolean> movingBackwards,
            Optional<URDragonEntity.TurningState> turningState,
            // flying dragon conditions
            Optional<Boolean> flying,
            Optional<FlyingDragon.TiltState> tiltState,
            Optional<Boolean> gliding,
            // head mount dragon conditions
            Optional<Boolean> ridingPlayer,
            // rideable dragon conditions
            Optional<Boolean> controlledByRider
    ) {
        public static final Codec<UseConditions> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.BOOL.optionalFieldOf("moving").forGetter(UseConditions::moving),
                Codec.BOOL.optionalFieldOf("moving_backwards").forGetter(UseConditions::movingBackwards),
                StringRepresentable.fromEnum(URDragonEntity.TurningState::values).optionalFieldOf("turning_state").forGetter(UseConditions::turningState),
                Codec.BOOL.optionalFieldOf("flying").forGetter(UseConditions::flying),
                StringRepresentable.fromEnum(FlyingDragon.TiltState::values).optionalFieldOf("tilt_state").forGetter(UseConditions::tiltState),
                Codec.BOOL.optionalFieldOf("gliding").forGetter(UseConditions::gliding),
                Codec.BOOL.optionalFieldOf("riding_player").forGetter(UseConditions::ridingPlayer),
                Codec.BOOL.optionalFieldOf("controlled_by_rider").forGetter(UseConditions::controlledByRider)
        ).apply(i, UseConditions::new));

        public boolean test(URDragonEntity entity) {
            if (moving.isPresent() && moving.get() != entity.isMoving()) return false;
            if (movingBackwards.isPresent() && movingBackwards.get() != entity.isMovingBackwards()) return false;
            if (turningState.isPresent() && turningState.get() != entity.getTurningState()) return false;
            if (flying.isPresent() || tiltState.isPresent() || gliding.isPresent()) {
                if (!(entity instanceof FlyingDragon flyingDragon)) return false;
                if (flying.isPresent() && flying.get() != flyingDragon.isFlying()) return false;
                if (tiltState.isPresent() && tiltState.get() != flyingDragon.getTiltState()) return false;
                if (gliding.isPresent() && gliding.get() != flyingDragon.isFlyGliding()) return false;
            }
            if (ridingPlayer.isPresent()) {
                if (!(entity instanceof HeadMountDragon)) return false;
                if (ridingPlayer.isPresent() && ridingPlayer.get() != entity.vehicle instanceof Player) return false;
            }
            if (controlledByRider.isPresent()) {
                if (!(entity instanceof URRideableDragonEntity)) return false;
                if (controlledByRider.isPresent() && controlledByRider.get() != entity.hasControllingPassenger()) return false;
            }
            return true;
        }

        public Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Boolean moving;
            private Boolean movingBackwards;
            private URDragonEntity.TurningState turningState;
            private Boolean flying;
            private FlyingDragon.TiltState tiltState;
            private Boolean gliding;
            private Boolean ridingPlayer;
            private Boolean controlledByRider;

            private Builder(){}

            public Builder moving(boolean moving) {
                this.moving = moving;
                return this;
            }

            public Builder movingBackwards(boolean movingBackwards) {
                this.movingBackwards = movingBackwards;
                return this;
            }

            public Builder turningState(URDragonEntity.TurningState turningState) {
                this.turningState = turningState;
                return this;
            }

            public Builder flying(boolean flying) {
                this.flying = flying;
                return this;
            }

            public Builder tiltState(FlyingDragon.TiltState tiltState) {
                this.tiltState = tiltState;
                return this;
            }

            public Builder gliding(boolean gliding) {
                this.gliding = gliding;
                return this;
            }

            public Builder ridingPlayer(boolean ridingPlayer) {
                this.ridingPlayer = ridingPlayer;
                return this;
            }

            public Builder controlledByRider(boolean controlledByRider) {
                this.controlledByRider = controlledByRider;
                return this;
            }

            public UseConditions build() {
                return new UseConditions(
                        Optional.ofNullable(moving),
                        Optional.ofNullable(movingBackwards),
                        Optional.ofNullable(turningState),
                        Optional.ofNullable(flying),
                        Optional.ofNullable(tiltState),
                        Optional.ofNullable(gliding),
                        Optional.ofNullable(ridingPlayer),
                        Optional.ofNullable(controlledByRider)
                );
            }
        }
    }
}
