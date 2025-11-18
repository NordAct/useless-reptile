package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.NotNull;

public record GUIEntityToRenderS2CPacket(int id) implements CustomPacketPayload {
    public static final ResourceLocation ID = UselessReptile.id("gui_entity_to_render_packet");
    public static final Type<GUIEntityToRenderS2CPacket> PACKET_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, GUIEntityToRenderS2CPacket> PACKET_CODEC = ByteBufCodecs.INT.map(GUIEntityToRenderS2CPacket::new, GUIEntityToRenderS2CPacket::id).cast();

    public static void send(ServerPlayer player, URDragonEntity dragon) {
        ServerPlayNetworking.send(player, new GUIEntityToRenderS2CPacket(dragon.getId()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
