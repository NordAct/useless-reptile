package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.common.network.GUIEntityToRenderS2CPacket;

public class GUIEntityToRenderPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(GUIEntityToRenderS2CPacket.PACKET_ID, (packet, context) -> {
            URDragonScreen.entityToRenderID = packet.id();
        });
    }
}
