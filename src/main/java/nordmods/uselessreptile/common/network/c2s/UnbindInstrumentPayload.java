package nordmods.uselessreptile.common.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

public record UnbindInstrumentPayload(int id) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("unbind_instrument");
    public static final Type<UnbindInstrumentPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnbindInstrumentPayload> PACKET_CODEC =
            StreamCodec.of(UnbindInstrumentPayload::write, UnbindInstrumentPayload::read);

    private static UnbindInstrumentPayload read(RegistryFriendlyByteBuf buffer) {
        int id = buffer.readInt();
        return new UnbindInstrumentPayload(id);
    }

    private static void write(RegistryFriendlyByteBuf buf, UnbindInstrumentPayload packet) {
        buf.writeInt(packet.id);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
