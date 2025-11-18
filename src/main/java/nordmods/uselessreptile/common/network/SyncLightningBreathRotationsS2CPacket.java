package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;

public record SyncLightningBreathRotationsS2CPacket(int[] beamIDs, float pitch, float yaw) implements CustomPacketPayload {
    public static final ResourceLocation ID = UselessReptile.id("sync_lightning_beam_rotations_packet");
    public static final Type<SyncLightningBreathRotationsS2CPacket> PACKET_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLightningBreathRotationsS2CPacket> PACKET_CODEC = StreamCodec.of(SyncLightningBreathRotationsS2CPacket::write, SyncLightningBreathRotationsS2CPacket::read);

    public static void send(ServerPlayer player, int[] beamIDs, float pitch, float yaw) {
        ServerPlayNetworking.send(player, new SyncLightningBreathRotationsS2CPacket(beamIDs, pitch, yaw));
    }

    private static SyncLightningBreathRotationsS2CPacket read(RegistryFriendlyByteBuf buffer) {
        int amount = buffer.readInt();
        int[] beams = new int[amount];
        for (int i = 0; i < amount; i++) beams[i] = buffer.readInt();
        float pitch = buffer.readFloat();
        float yaw = buffer.readFloat();
        return new SyncLightningBreathRotationsS2CPacket(beams, pitch, yaw);
    }

    private static void write(RegistryFriendlyByteBuf buf, SyncLightningBreathRotationsS2CPacket packet) {
        buf.writeInt(packet.beamIDs.length);
        for (int id : packet.beamIDs) buf.writeInt(id);
        buf.writeFloat(packet.pitch);
        buf.writeFloat(packet.yaw);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
