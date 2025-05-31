package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.entity.base.URMultipartEntity;
import nordmods.uselessreptile.common.network.SyncPartPosS2CPacket;

public class SyncPartPosPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPartPosS2CPacket.PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.ownerId());
            if (!(entity instanceof URMultipartEntity multipartEntity)) return;
            multipartEntity.updateNextPartPos(packet.relativePos());
        });
    }
}
