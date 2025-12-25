package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.NotNull;

public record GUIEntityToRenderPayload(int id) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("gui_entity_to_render");
    public static final Type<GUIEntityToRenderPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, GUIEntityToRenderPayload> PACKET_CODEC = ByteBufCodecs.INT.map(GUIEntityToRenderPayload::new, GUIEntityToRenderPayload::id).cast();

    public static void send(ServerPlayer player, URDragonEntity dragon) {
        ServerPlayNetworking.send(player, new GUIEntityToRenderPayload(dragon.getId()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
