package nordmods.uselessreptile.client.init;

import nordmods.uselessreptile.client.network.*;

public class URPacketEvents {
    public static void init() {
        KeyInputPacket.init();
        LiftoffParticlesPacket.init();
        PosSyncPacket.init();
        GUIEntityToRenderPacket.init();
        InstrumentSoundBoundMessagePacket.init();
        SyncLightningBreathRotationsPacket.init();
    }
}
