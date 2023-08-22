package nordmods.uselessreptile.client.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import nordmods.uselessreptile.common.network.InteractPartOwnerC2SPacket;

public class InteractPartOwnerPacket {
    public static void init() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() && !player.isSpectator() && entity instanceof URDragonPart part && part.owner != null && !(player.getVehicle() == part.owner && part.owner.getOwner() == player)) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(part.owner.getId());
                buf.writeInt(player.getId());
                ClientPlayNetworking.send(InteractPartOwnerC2SPacket.INTERACT_PART_OWNER_PACKET, buf);
            }
            return ActionResult.PASS;
        });
    }
}
