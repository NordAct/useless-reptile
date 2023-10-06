package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import nordmods.uselessreptile.client.init.*;
import nordmods.uselessreptile.client.network.*;
import nordmods.uselessreptile.client.util.modelRedirect.ModelRedirectLoader;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelRedirectLoader.init();
        URClientEvents.init();
        URModelPredicates.init();
        URRenderers.init();
        URKeybinds.init();
        URScreens.init();

        KeyInputPacket.init();
        KeyInputSyncPacket.init();
        LiftoffParticlesPacket.init();
        PosSyncPacket.init();
        AttackTypeSyncPacket.init();
        GUIEntityToRenderPacket.init();
        AttackPartOwnerPacket.init();
        InteractPartOwnerPacket.init();
        InstrumentSoundBoundMessagePacket.init();
    }
}