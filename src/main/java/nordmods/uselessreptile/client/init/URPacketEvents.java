package nordmods.uselessreptile.client.init;

import nordmods.uselessreptile.client.network.*;

public class URPacketEvents {
    public static void init() {
        LiftoffParticlesPacket.init();
        GUIEntityToRenderPacket.init();
        SyncLightningBreathRotationsPacket.init();
    }
}
