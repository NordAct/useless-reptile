package nordmods.uselessreptile.common.entity.base;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public interface FlyingDragon {
    int getInAirTimer();
    int getMaxInAirTimer();
    void setInAirTimer(int state);
    boolean isFlying();
    void setFlying(boolean state);
    TiltState getTiltState();
    void setTiltState(TiltState state);
    void startToFly();
    float getVerticalSpeed();
    float getFlyingRotationSpeed();
    void forceFlightNextTick();
    boolean isFlyGliding();
    void setFlyGliding (boolean state);
    boolean shouldFlyDown();

    enum TiltState implements StringRepresentable {
        NONE("none"),
        UP("up"),
        DOWN("down")
        ;

        private final String name;

        public static final StreamCodec<ByteBuf, TiltState> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

        TiltState(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return name;
        }
    }
}
