package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.network.PosS2CSyncPacket;

public class PosSyncPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PosS2CSyncPacket.POS_SYNC_PACKET, (client, handler, buffer, sender) -> {
            Vec3d pos = new Vec3d(buffer.readVector3f());
            URRideableDragonEntity dragon = (URRideableDragonEntity) client.world.getEntityById(buffer.readInt());
            client.execute(() ->{
                if (dragon != null && dragon.getControllingPassenger() != client.player) dragon.setPosition(pos);
            });
        });
    }
}
