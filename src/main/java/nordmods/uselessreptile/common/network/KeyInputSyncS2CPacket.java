package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class KeyInputSyncS2CPacket {
    public static final Identifier KEY_INPUT_SYNC_PACKET = new Identifier(UselessReptile.MODID, "key_input_sync_packet");

    public static void send(ServerPlayerEntity player, URRideableDragonEntity dragon) {
        if (dragon.canBeControlledByRider()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(dragon.isJumpPressed()); //0 - jump
            buf.writeBoolean(dragon.isMoveForwardPressed()); //1 - forward
            buf.writeBoolean(dragon.isMoveBackPressed()); //2 - back
            buf.writeBoolean(dragon.isSprintPressed()); //3 - sprint
            buf.writeBoolean(dragon.isSecondaryAttackPressed); //4 - melee attack
            buf.writeBoolean(dragon.isPrimaryAttackPressed); //5 - range attack
            buf.writeBoolean(dragon.isDownPressed()); //6 - fly down

            buf.writeInt(dragon.getId());
            ServerPlayNetworking.send(player, KEY_INPUT_SYNC_PACKET, buf);
        }
    }
}
