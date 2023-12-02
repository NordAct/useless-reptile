package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public class SyncLightningBeamRotationsS2CPacket {
    public static final Identifier SYNC_LIGHTNING_BEAM_ROTATIONS_PACKET = new Identifier(UselessReptile.MODID, "sync_lightning_beam_rotations_packet");

    public static void send(ServerPlayerEntity player, int[] beamIDs, float pitch, float yaw) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(beamIDs.length);
        for (int id : beamIDs) buf.writeInt(id);
        buf.writeFloat(pitch);
        buf.writeFloat(yaw);
        ServerPlayNetworking.send(player, SYNC_LIGHTNING_BEAM_ROTATIONS_PACKET, buf);
    }
}
