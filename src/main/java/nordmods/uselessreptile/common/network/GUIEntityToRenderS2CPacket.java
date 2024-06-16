package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public record GUIEntityToRenderS2CPacket(int id) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("gui_entity_to_render_packet");
    public static final CustomPayload.Id<GUIEntityToRenderS2CPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, GUIEntityToRenderS2CPacket> PACKET_CODEC = PacketCodecs.INTEGER.xmap(GUIEntityToRenderS2CPacket::new, GUIEntityToRenderS2CPacket::id).cast();

    public static void send(ServerPlayerEntity player, URDragonEntity dragon) {
        ServerPlayNetworking.send(player, new GUIEntityToRenderS2CPacket(dragon.getId()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
