package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import nordmods.uselessreptile.common.network.InstrumentSoundBoundMessageS2CPacket;


public class InstrumentSoundBoundMessagePacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(InstrumentSoundBoundMessageS2CPacket.INSTRUMENT_SOUND_BOUND_MESSAGE, (client, handler, buffer, sender) -> {
            Text entityName = buffer.readText();
            Text instrument = buffer.readText();
            client.execute(() ->{
                client.inGameHud.setOverlayMessage(Text.translatable
                        ("other.uselessreptile.sound_respond", entityName, instrument), false);
                if (client.player != null) client.player.playSound(SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.NEUTRAL, 0.2f, 2);
            });
        });
    }
}
