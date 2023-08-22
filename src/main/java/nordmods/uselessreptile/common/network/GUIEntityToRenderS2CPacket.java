package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class GUIEntityToRenderS2CPacket {
    public static final Identifier GUI_ENTITY_TO_RENDER = new Identifier(UselessReptile.MODID, "gui_entity_to_render_packet");

    public static void send(ServerPlayerEntity player, URDragonEntity dragon) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(dragon.getId());
        ServerPlayNetworking.send(player, GUI_ENTITY_TO_RENDER, buf);
    }
}
