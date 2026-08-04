package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URClientPayloadHandlers;
import nordmods.uselessreptile.client.init.URKeyMappings;
import nordmods.uselessreptile.client.init.URRenderPipelines;
import nordmods.uselessreptile.client.init.URRenderers;
import nordmods.uselessreptile.client.util.CurrentFluteModeProperty;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SelectItemModelProperties.ID_MAPPER.put(UselessReptile.id("current_flute_mode"), CurrentFluteModeProperty.TYPE);
        URClientConfig.init();
        URRenderers.init();
        URRenderPipelines.init();
        URKeyMappings.init();
        URClientPayloadHandlers.init();
    }
}