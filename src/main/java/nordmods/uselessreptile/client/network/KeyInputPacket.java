package nordmods.uselessreptile.client.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;

@Environment(EnvType.CLIENT)
public class KeyInputPacket {
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null)
                if (player.getVehicle() instanceof URRideableDragonEntity dragon) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeBoolean(dragon.isJumpPressed()); //0 - jump
                    buf.writeBoolean(dragon.isMoveForwardPressed()); //1 - forward
                    buf.writeBoolean(dragon.isMoveBackPressed()); //2 - back
                    buf.writeBoolean(dragon.isSprintPressed()); //3 - sprint
                    buf.writeBoolean(dragon.isSecondaryAttackPressed); //4 - melee attack
                    buf.writeBoolean(dragon.isPrimaryAttackPressed); //5 - range attack
                    buf.writeBoolean(dragon.isDownPressed()); //6 - fly down
                    buf.writeInt(dragon.getId());
                    ClientPlayNetworking.send(KeyInputC2SPacket.KEY_INPUT_PACKET, buf);
                }
        });
    }
}
