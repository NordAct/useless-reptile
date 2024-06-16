package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.network.PosSyncS2CPacket;

public class PosSyncPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PosSyncS2CPacket.PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.id());
            if (entity instanceof URDragonEntity dragon) dragon.setPosition(packet.pos());
        });
    }
}
