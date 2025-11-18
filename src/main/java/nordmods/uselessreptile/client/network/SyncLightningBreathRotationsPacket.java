package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.network.SyncLightningBreathRotationsS2CPacket;

public class SyncLightningBreathRotationsPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SyncLightningBreathRotationsS2CPacket.PACKET_ID, (packet, context) -> {
                for (int id : packet.beamIDs()) {
                    Entity entity = context.player().level().getEntity(id);
                    if (!(entity instanceof LightningBreathEntity lightningBreathEntity)) continue;
                    if (id == packet.beamIDs()[0]) lightningBreathEntity.setBeamLength(packet.beamIDs().length);
                    entity.setXRot(packet.pitch());
                    entity.setYRot(packet.yaw());
                }
        });
    }
}
