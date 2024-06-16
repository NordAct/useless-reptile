package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record FluteComponent(byte mode) {
    public static final FluteComponent DEFAULT = new FluteComponent((byte) 0);
    public static final Codec<FluteComponent> CODEC = Codec.BYTE.xmap(FluteComponent::new, FluteComponent::mode);
    public static final PacketCodec<ByteBuf, FluteComponent> PACKET_CODEC = PacketCodecs.BYTE.xmap(FluteComponent::new, FluteComponent::mode);
}
