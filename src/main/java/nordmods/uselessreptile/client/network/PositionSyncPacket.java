package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.network.PositionSyncS2CPacket;

public class PositionSyncPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PositionSyncS2CPacket.PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.id());
            if (entity != null) {
                double x = packet.x();
                double y = packet.y();
                double z = packet.z();
                float yaw = packet.yaw();
                float pitch = packet.pitch();
                //entity.refreshPositionAndAngles(packet.x(), packet.y(), packet.z(), packet.yaw(), packet.pitch());
                entity.setPos(x, y, z);
                entity.setYaw(yaw);
                entity.setPitch(pitch);
            }
        });
    }
}
