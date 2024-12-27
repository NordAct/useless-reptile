package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import nordmods.uselessreptile.common.network.*;

public class URPackets {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(GUIEntityToRenderS2CPacket.PACKET_ID, GUIEntityToRenderS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(LiftoffParticlesS2CPacket.PACKET_ID, LiftoffParticlesS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncLightningBreathRotationsS2CPacket.PACKET_ID, SyncLightningBreathRotationsS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(KeyInputC2SPacket.PACKET_ID, KeyInputC2SPacket.PACKET_CODEC);
        KeyInputC2SPacket.init();
    }
}
