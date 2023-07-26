package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class PosS2CSyncPacket {
    public static final Identifier POS_SYNC_PACKET = new Identifier(UselessReptile.MODID, "pos_sync_packet");

    public static void send(ServerPlayerEntity player, URRideableDragonEntity dragon) {
        if (dragon.canBeControlledByRider()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVector3f(dragon.getPos().toVector3f());
            buf.writeInt(dragon.getId());
            ServerPlayNetworking.send(player, POS_SYNC_PACKET, buf);
        }
    }
}
