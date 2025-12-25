package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import org.jetbrains.annotations.NotNull;

public record SyncLightningBreathRotationsPayload(int[] beamIDs, float pitch, float yaw) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("sync_lightning_beam_rotations");
    public static final Type<SyncLightningBreathRotationsPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLightningBreathRotationsPayload> PACKET_CODEC = StreamCodec.of(SyncLightningBreathRotationsPayload::write, SyncLightningBreathRotationsPayload::read);

    public static void send(ServerPlayer player, int[] beamIDs, float pitch, float yaw) {
        ServerPlayNetworking.send(player, new SyncLightningBreathRotationsPayload(beamIDs, pitch, yaw));
    }

    private static SyncLightningBreathRotationsPayload read(RegistryFriendlyByteBuf buffer) {
        int amount = buffer.readInt();
        int[] beams = new int[amount];
        for (int i = 0; i < amount; i++) beams[i] = buffer.readInt();
        float pitch = buffer.readFloat();
        float yaw = buffer.readFloat();
        return new SyncLightningBreathRotationsPayload(beams, pitch, yaw);
    }

    private static void write(RegistryFriendlyByteBuf buf, SyncLightningBreathRotationsPayload packet) {
        buf.writeInt(packet.beamIDs.length);
        for (int id : packet.beamIDs) buf.writeInt(id);
        buf.writeFloat(packet.pitch);
        buf.writeFloat(packet.yaw);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
