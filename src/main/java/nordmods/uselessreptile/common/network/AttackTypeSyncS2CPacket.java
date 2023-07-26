package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class AttackTypeSyncS2CPacket {
    public static final Identifier ATTACK_TYPE_PACKET = new Identifier(UselessReptile.MODID, "attack_type_sync_packet");

    public static void send(ServerPlayerEntity player, URDragonEntity dragon) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(dragon.attackType);
        buf.writeInt(dragon.getId());
        ServerPlayNetworking.send(player, ATTACK_TYPE_PACKET, buf);
    }
}
