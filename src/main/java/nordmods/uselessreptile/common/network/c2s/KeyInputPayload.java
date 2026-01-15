package nordmods.uselessreptile.common.network.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.uselessreptile.UselessReptile;
import org.jspecify.annotations.NonNull;

public record KeyInputPayload(
        boolean jump,
        boolean forward,
        boolean back,
        boolean sprint,
        boolean secondaryAttack,
        boolean primaryAttack,
        boolean down,
        boolean freeLook,
        int id
) implements CustomPacketPayload{
    public static final Identifier ID = UselessReptile.id("key_input");
    public static final Type<KeyInputPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyInputPayload> PACKET_CODEC =
            StreamCodec.of(KeyInputPayload::write, KeyInputPayload::read);

    private static KeyInputPayload read(RegistryFriendlyByteBuf buffer) {
        boolean jump = buffer.readBoolean();
        boolean forward = buffer.readBoolean();
        boolean back = buffer.readBoolean();
        boolean sprint = buffer.readBoolean();
        boolean secondaryAttack = buffer.readBoolean();
        boolean primaryAttack = buffer.readBoolean();
        boolean down = buffer.readBoolean();
        boolean freeLook = buffer.readBoolean();
        int id = buffer.readInt();
        return new KeyInputPayload(jump, forward, back, sprint, secondaryAttack, primaryAttack, down, freeLook, id);
    }

    private static void write(RegistryFriendlyByteBuf buf, KeyInputPayload packet) {
        buf.writeBoolean(packet.jump);
        buf.writeBoolean(packet.forward);
        buf.writeBoolean(packet.back);
        buf.writeBoolean(packet.sprint);
        buf.writeBoolean(packet.secondaryAttack);
        buf.writeBoolean(packet.primaryAttack);
        buf.writeBoolean(packet.down);
        buf.writeBoolean(packet.freeLook);
        buf.writeInt(packet.id);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
