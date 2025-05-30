package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.network.SyncPartPosS2CPacket;

public class SyncPartPosPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPartPosS2CPacket.PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.ownerId());
            if (!(entity instanceof MultipartEntity multipartEntity)) return;
            for (int i = 0; i < packet.relativePos().length; i++) {
                Vec3d relativePos = packet.relativePos()[i];
                multipartEntity.getParts()[i].setRelativePos(relativePos.x, relativePos.y, relativePos.z, 0, entity.getYaw() - 180f);
            }
        });
    }
}
