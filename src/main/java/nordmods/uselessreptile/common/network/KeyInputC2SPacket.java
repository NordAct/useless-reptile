package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public record KeyInputC2SPacket(boolean jump, boolean forward, boolean back, boolean sprint, boolean secondaryAttack, boolean primaryAttack, boolean down, int id) implements CustomPayload{
    public static final Identifier ID = UselessReptile.id("key_input");
    public static final CustomPayload.Id<KeyInputC2SPacket> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, KeyInputC2SPacket> PACKET_CODEC =
            PacketCodec.ofStatic(KeyInputC2SPacket::write, KeyInputC2SPacket::read);

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.id);
            if (entity instanceof URRideableDragonEntity dragon) {
                dragon.isSecondaryAttackPressed = packet.secondaryAttack;
                dragon.isPrimaryAttackPressed = packet.primaryAttack;

                dragon.updateInputs(packet.forward, packet.back, packet.jump, packet.down, packet.sprint);
            }
        });
    }

    private static KeyInputC2SPacket read (RegistryByteBuf buffer) {
        boolean jump = buffer.readBoolean();
        boolean forward = buffer.readBoolean();
        boolean back = buffer.readBoolean();
        boolean sprint = buffer.readBoolean();
        boolean secondaryAttack = buffer.readBoolean();
        boolean primaryAttack = buffer.readBoolean();
        boolean down = buffer.readBoolean();
        int id = buffer.readInt();
        return new KeyInputC2SPacket(jump, forward, back, sprint, secondaryAttack, primaryAttack, down, id);
    }

    private static void write(RegistryByteBuf buf, KeyInputC2SPacket packet) {
        buf.writeBoolean(packet.jump);
        buf.writeBoolean(packet.forward);
        buf.writeBoolean(packet.back);
        buf.writeBoolean(packet.sprint);
        buf.writeBoolean(packet.secondaryAttack);
        buf.writeBoolean(packet.primaryAttack);
        buf.writeBoolean(packet.down);
        buf.writeInt(packet.id);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
