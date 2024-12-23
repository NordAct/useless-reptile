package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record VortexHornCapacityComponent(int capacity) {
    public static final VortexHornCapacityComponent DEFAULT = new VortexHornCapacityComponent(0);
    public static final Codec<VortexHornCapacityComponent> CODEC = Codec.INT.xmap(VortexHornCapacityComponent::new, VortexHornCapacityComponent::capacity);
    public static final PacketCodec<ByteBuf, VortexHornCapacityComponent> PACKET_CODEC = PacketCodecs.INTEGER.xmap(VortexHornCapacityComponent::new, VortexHornCapacityComponent::capacity);
}
