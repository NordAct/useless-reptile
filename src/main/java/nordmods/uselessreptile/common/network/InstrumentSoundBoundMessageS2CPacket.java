package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public record InstrumentSoundBoundMessageS2CPacket(Text message) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("instrument_sound_bound_packet");
    public static final CustomPayload.Id<InstrumentSoundBoundMessageS2CPacket> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, InstrumentSoundBoundMessageS2CPacket> PACKET_CODEC = TextCodecs.PACKET_CODEC.xmap(InstrumentSoundBoundMessageS2CPacket::new, InstrumentSoundBoundMessageS2CPacket::message).cast();


    public static void send(ServerPlayerEntity player, URDragonEntity dragon, Text instrument) {
        Text text = Text.translatable("other.uselessreptile.sound_respond", dragon.getName(), instrument);
        ServerPlayNetworking.send(player, new InstrumentSoundBoundMessageS2CPacket(text));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
