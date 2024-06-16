package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import nordmods.uselessreptile.common.network.InstrumentSoundBoundMessageS2CPacket;


public class InstrumentSoundBoundMessagePacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(InstrumentSoundBoundMessageS2CPacket.PACKET_ID, (packet, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() ->{
                client.inGameHud.setOverlayMessage(packet.message(), false);
                if (client.player != null) client.player.playSound(SoundEvents.BLOCK_COMPARATOR_CLICK, 0.2f, 2);
            });
        });
    }
}
