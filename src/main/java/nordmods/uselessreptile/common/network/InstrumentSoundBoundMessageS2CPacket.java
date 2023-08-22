package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class InstrumentSoundBoundMessageS2CPacket {
    public static final Identifier INSTRUMENT_SOUND_BOUND_MESSAGE = new Identifier(UselessReptile.MODID, "instrument_sound_bound_packet");

    public static void send(ServerPlayerEntity player, URDragonEntity dragon, Text instrument) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeText(dragon.getName());
        buf.writeText(instrument);
        ServerPlayNetworking.send(player, INSTRUMENT_SOUND_BOUND_MESSAGE, buf);
    }
}
