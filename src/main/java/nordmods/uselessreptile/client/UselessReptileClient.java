package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import nordmods.uselessreptile.client.init.*;
import nordmods.uselessreptile.client.network.*;
import nordmods.uselessreptile.client.util.model_redirect.ModelRedirectLoader;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelRedirectLoader.init();
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
        InstrumentSoundBoundMessagePacket.init();
    }
}