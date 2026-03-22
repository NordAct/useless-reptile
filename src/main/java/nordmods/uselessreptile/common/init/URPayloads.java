package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import nordmods.uselessreptile.common.network.c2s.*;
import nordmods.uselessreptile.common.network.s2c.OpenDragonInventoryPayload;
import nordmods.uselessreptile.common.network.s2c.LiftoffParticlesPayload;
import nordmods.uselessreptile.common.network.s2c.OpenVariantChangingOrbScreenPayload;
import nordmods.uselessreptile.common.network.s2c.SyncLightningBreathRotationsPayload;

public class URPayloads {
    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(OpenDragonInventoryPayload.PAYLOAD_ID, OpenDragonInventoryPayload.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(LiftoffParticlesPayload.PAYLOAD_ID, LiftoffParticlesPayload.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncLightningBreathRotationsPayload.PAYLOAD_ID, SyncLightningBreathRotationsPayload.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenVariantChangingOrbScreenPayload.PAYLOAD_ID, OpenVariantChangingOrbScreenPayload.PACKET_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(KeyInputPayload.PAYLOAD_ID, KeyInputPayload.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RequestLiftoffPayload.PAYLOAD_ID, RequestLiftoffPayload.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(OrderPayload.PAYLOAD_ID, OrderPayload.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UnbindInstrumentPayload.PAYLOAD_ID, UnbindInstrumentPayload.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChangeWanderRadiusPayload.PAYLOAD_ID, ChangeWanderRadiusPayload.PACKET_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetVariantChangingOrbVariantPayload.PAYLOAD_ID, SetVariantChangingOrbVariantPayload.PACKET_CODEC);

        URPayloadHandlers.init();
    }
}
