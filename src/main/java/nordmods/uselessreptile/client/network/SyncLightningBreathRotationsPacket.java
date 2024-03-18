package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.network.SyncLightningBreathRotationsS2CPacket;

public class SyncLightningBreathRotationsPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncLightningBreathRotationsS2CPacket.SYNC_LIGHTNING_BEAM_ROTATIONS_PACKET, (client, handler, buffer, sender) -> {
            if (client.world == null) return;
            int length = buffer.readInt();
            int[] entities = new int[length];
            for (int i = 0; i < length; i++) {
                int id = buffer.readInt();
                entities[i] = id;
            }
            float pitch = buffer.readFloat();
            float yaw = buffer.readFloat();
            client.execute(() -> {
                for (int id : entities) {
                    LightningBreathEntity entity = (LightningBreathEntity) client.world.getEntityById(id);
                    if (entity == null) continue;
                    entity.setPitch(pitch);
                    entity.setYaw(yaw);
                }
            });
        });
    }
}
