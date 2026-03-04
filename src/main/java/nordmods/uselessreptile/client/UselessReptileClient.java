package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.*;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        URClientConfig.init();
        URRenderers.init();
        URRenderPipelines.init();
        URKeyMappings.init();
        URClientPayloadHandlers.init();
    }
}