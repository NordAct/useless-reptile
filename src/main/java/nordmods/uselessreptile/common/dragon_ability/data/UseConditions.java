package nordmods.uselessreptile.common.dragon_ability.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

import java.util.Optional;

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
            if (ridingPlayer.get() != entity.vehicle instanceof Player) return false;
        }
        if (controlledByRider.isPresent()) {
            if (!(entity instanceof URRideableDragonEntity)) return false;
            if (controlledByRider.get() != entity.hasControllingPassenger()) return false;
        }
        return true;
    }

    public static Builder builder() {
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

        private Builder() {
        }

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
