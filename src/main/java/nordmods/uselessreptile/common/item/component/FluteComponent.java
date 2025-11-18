package nordmods.uselessreptile.common.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluteComponent(String mode) {
    public static final FluteComponent DEFAULT = new FluteComponent("call");
    public static final Codec<FluteComponent> CODEC = Codec.STRING.xmap(FluteComponent::new, FluteComponent::mode);
    public static final StreamCodec<ByteBuf, FluteComponent> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(FluteComponent::new, FluteComponent::mode);
}
