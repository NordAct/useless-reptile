package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import nordmods.uselessreptile.common.network.URPayloadHandlers;
import nordmods.uselessreptile.common.network.c2s.KeyInputPayload;
import nordmods.uselessreptile.common.network.c2s.RequestLiftoffPayload;
import nordmods.uselessreptile.common.network.s2c.OpenDragonInventoryPayload;
import nordmods.uselessreptile.common.network.s2c.LiftoffParticlesPayload;
import nordmods.uselessreptile.common.network.s2c.SyncLightningBreathRotationsPayload;

public class URPayloads {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(OpenDragonInventoryPayload.PAYLOAD_ID, OpenDragonInventoryPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(LiftoffParticlesPayload.PAYLOAD_ID, LiftoffParticlesPayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncLightningBreathRotationsPayload.PAYLOAD_ID, SyncLightningBreathRotationsPayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(KeyInputPayload.PAYLOAD_ID, KeyInputPayload.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestLiftoffPayload.PAYLOAD_ID, RequestLiftoffPayload.PACKET_CODEC);

        URPayloadHandlers.init();
    }
}
