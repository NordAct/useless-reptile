package nordmods.uselessreptile.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.*;
import nordmods.uselessreptile.client.util.model_data.DragonModelDataReloadListener;
import nordmods.uselessreptile.client.util.model_data.EquipmentModelDataReloadListener;

@Environment(EnvType.CLIENT)
public class UselessReptileClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        URClientConfig.init();
        DragonModelDataReloadListener.init();
        EquipmentModelDataReloadListener.init();
        URModelPredicates.init();
        URRenderers.init();
        URKeybinds.init();
        URScreens.init();
        URPacketEvents.init();
    }
}