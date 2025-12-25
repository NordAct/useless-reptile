package nordmods.uselessreptile.common.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import org.jetbrains.annotations.NotNull;

public record RequestLiftoffPayload(int id) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("request_liftoff");
    public static final Type<RequestLiftoffPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestLiftoffPayload> PACKET_CODEC =
            StreamCodec.of(RequestLiftoffPayload::write, RequestLiftoffPayload::read);

    private static RequestLiftoffPayload read(RegistryFriendlyByteBuf buffer) {
        int id = buffer.readInt();
        return new RequestLiftoffPayload(id);
    }

    private static void write(RegistryFriendlyByteBuf buf, RequestLiftoffPayload packet) {
        buf.writeInt(packet.id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
