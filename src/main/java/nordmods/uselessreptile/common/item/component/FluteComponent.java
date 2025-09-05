package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record FluteComponent(String mode) {
    public static final FluteComponent DEFAULT = new FluteComponent("call");
    public static final Codec<FluteComponent> CODEC = Codec.STRING.xmap(FluteComponent::new, FluteComponent::mode);
    public static final PacketCodec<ByteBuf, FluteComponent> PACKET_CODEC = PacketCodecs.STRING.xmap(FluteComponent::new, FluteComponent::mode);
}
