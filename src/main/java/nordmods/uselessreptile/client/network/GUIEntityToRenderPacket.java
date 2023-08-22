package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;

public class GUIEntityToRenderPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(GUIEntityToRenderS2CPacket.GUI_ENTITY_TO_RENDER, (client, handler, buffer, sender) -> {
            int id = buffer.readInt();
            client.execute(() -> URDragonScreen.entityToRenderID = id);
        });
    }
}
