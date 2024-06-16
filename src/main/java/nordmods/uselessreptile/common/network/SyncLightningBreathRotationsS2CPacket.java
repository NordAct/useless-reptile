package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public record SyncLightningBreathRotationsS2CPacket(int[] beamIDs, float pitch, float yaw) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("sync_lightning_beam_rotations_packet");
    public static final CustomPayload.Id<SyncLightningBreathRotationsS2CPacket> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SyncLightningBreathRotationsS2CPacket> PACKET_CODEC = PacketCodec.ofStatic(SyncLightningBreathRotationsS2CPacket::write, SyncLightningBreathRotationsS2CPacket::read);

    public static void send(ServerPlayerEntity player, int[] beamIDs, float pitch, float yaw) {
        ServerPlayNetworking.send(player, new SyncLightningBreathRotationsS2CPacket(beamIDs, pitch, yaw));
    }

    private static SyncLightningBreathRotationsS2CPacket read(RegistryByteBuf buffer) {
        int amount = buffer.readInt();
        int[] beams = new int[amount];
        for (int i = 0; i < amount; i++) beams[i] = buffer.readInt();
        float pitch = buffer.readFloat();
        float yaw = buffer.readFloat();
        return new SyncLightningBreathRotationsS2CPacket(beams, pitch, yaw);
    }

    private static void write(RegistryByteBuf buf, SyncLightningBreathRotationsS2CPacket packet) {
        buf.writeInt(packet.beamIDs.length);
        for (int id : packet.beamIDs) buf.writeInt(id);
        buf.writeFloat(packet.pitch);
        buf.writeFloat(packet.yaw);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
