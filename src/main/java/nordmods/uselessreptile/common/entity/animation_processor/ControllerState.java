package nordmods.uselessreptile.common.entity.animation_processor;

import io.netty.buffer.ByteBuf;
import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ControllerState(int controllerOrdinal, List<PlayingAnimation> playingAnimations) {
    public static final StreamCodec<ByteBuf, ControllerState> STREAM_CODEC = StreamCodec.of(
            (buf, s) -> {
                buf.writeInt(s.controllerOrdinal());
                PlayingAnimation.LIST_STREAM_CODEC.encode(buf, s.playingAnimations());
            },
            buf -> new ControllerState(
                    buf.readInt(),
                    PlayingAnimation.LIST_STREAM_CODEC.decode(buf)
            )
    );

    public static final StreamCodec<ByteBuf, List<ControllerState>> LIST_STREAM_CODEC = ControllerState.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static List<ControllerState> collectControllerStates(List<BRAnimationController> controllers) {
        List<ControllerState> list = new ArrayList<>();

        for (int i = 0; i < controllers.size(); i++) {
            BRAnimationController controller = controllers.get(i);
            List<PlayingAnimation> playingAnimations = new ArrayList<>();
            for (BRPlayingAnimation anim : controller.getPlayingAnimations()) {
                if (!anim.isDone() || !anim.canClearOut()) playingAnimations.add(new PlayingAnimation(
                        anim.getAnimation().name(),
                        anim.getActualAnimationTime(),
                        anim.getSpeed(),
                        anim.isPaused()
                ));
            }
            list.add(new ControllerState(i, playingAnimations));
        }
        return list;
    }

    public static void applyControllerStates(List<ControllerState> states, List<BRAnimationController> controllers) {
        for (ControllerState controllerState : states) {
            BRAnimationController controller = controllers.get(controllerState.controllerOrdinal());
            if (controller == null) continue;

            Set<String> shouldPlay = new HashSet<>();
            for (PlayingAnimation playingAnimation : controllerState.playingAnimations()) {
                shouldPlay.add(playingAnimation.name());
                PlayingAnimation.applyPlayingAnimation(controller, playingAnimation);
            }

            for (BRPlayingAnimation animation : controller.getPlayingAnimations()) {
                String name = animation.getAnimation().name();
                if (shouldPlay.contains(name)) continue;
                if (animation.isFinished()) continue;
                animation.stop();
            }
        }
    }

    public record PlayingAnimation(
            String name,
            float time,
            float speed,
            boolean paused
    ) {
        public static final StreamCodec<ByteBuf, PlayingAnimation> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public PlayingAnimation decode(ByteBuf input) {
                String name = ByteBufCodecs.STRING_UTF8.decode(input);
                float time = input.readFloat();
                float speed = input.readFloat();
                boolean paused = input.readBoolean();
                return new PlayingAnimation(name, time, speed, paused);
            }

            @Override
            public void encode(ByteBuf output, PlayingAnimation value) {
                ByteBufCodecs.STRING_UTF8.encode(output, value.name());
                output.writeFloat(value.time());
                output.writeFloat(value.speed());
                output.writeBoolean(value.paused());
            }
        };

        public static final StreamCodec<ByteBuf, List<PlayingAnimation>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

        /// Plays animation or synchronizes time for animation if difference between server and client time is too noticeable
        /// @param controller controller where animation is supposed to be played at
        /// @param playingAnimation animation to play
        public static void applyPlayingAnimation(BRAnimationController controller, PlayingAnimation playingAnimation) {
            BRPlayingAnimation animation = controller.getAnimation(playingAnimation.name());

            if (animation == null) { //todo: come up with a way to "resume" animations that does not mess up transitions
                controller.playAnimation(playingAnimation.name());
                animation = controller.getAnimation(playingAnimation.name());
                if (animation == null) return;
                animation.setSpeed(playingAnimation.speed());
                animation.setPaused(playingAnimation.paused());
                return;
            }

            animation.setSpeed(playingAnimation.speed());
            if (playingAnimation.paused() != animation.isPaused()) animation.setPaused(playingAnimation.paused());
            if (animation.isTransitioningIn() || animation.isTransitioningOut()) return;

            float clientTime = animation.getActualAnimationTime();
            float serverTime = playingAnimation.time();
            float diff = serverTime - clientTime;

            float length = animation.getAnimation().animationLength();
            if (animation.getAnimation().loop() == AnimationData.Loop.LOOP && length > 0) {
                float clientRenderTime = animation.getRenderAnimationTime();
                float serverRenderTime = getRenderAnimationTime(serverTime, animation);
                diff = getDiff(clientRenderTime, serverRenderTime, length);
                serverTime = clientTime + diff;
            }

//            if (Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().player.sendOverlayMessage(Component.literal(diff + ""));
//            }

            float relativeDiff =  diff / Math.abs(length);
            float relativeDiffAbs = Math.abs(relativeDiff);
            if (relativeDiffAbs > 0.1f || Math.abs(diff) > 0.2f) {
                animation.setAnimationTime(serverTime);
                return;
            }
            animation.setSpeed(animation.getSpeed() * (1 + relativeDiff));
        }

        private static float getRenderAnimationTime(float actualTime, BRPlayingAnimation animation) {
            float time = Math.max(0, actualTime - animation.getTransitionInTime());
            float animationLength = animation.getAnimation().animationLength();
            return switch (animation.getAnimation().loop()) {
                case LOOP -> animationLength > 0 ? time % animationLength : time;
                case HOLD_ON_LAST_FRAME -> Math.min(time, animationLength);
                case NONE -> time;
            };
        }

        private static float getDiff(float current, float target, float length) {
            float diff = target - current;
            diff = ((diff + length / 2) % length + length) % length - length / 2;
            return diff;
        }
    }
}
