package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;

import java.util.stream.IntStream;

public record VortexHornCapacityComponent(int currentCapacity, int maxCapacity) {
    public static final VortexHornCapacityComponent DEFAULT = new VortexHornCapacityComponent(0, 0);
    public static final Codec<VortexHornCapacityComponent> CODEC = Codec.INT_STREAM.comapFlatMap(
            stream -> Util.fixedSize(stream, 2).map(values -> new VortexHornCapacityComponent(values[0], values[1])),
            component -> IntStream.of(component.currentCapacity(), component.maxCapacity())
    ).stable();
    public static final StreamCodec<ByteBuf, VortexHornCapacityComponent> PACKET_CODEC = StreamCodec.ofMember(
                (value, buf) -> {
                    buf.writeInt(value.currentCapacity());
                    buf.writeInt(value.maxCapacity());
                },
                (byteBuf) -> {
                    int current = byteBuf.readInt();
                    int max = byteBuf.readInt();
                    return new VortexHornCapacityComponent(current, max);
                }
            );
}
