package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class InteractPartOwnerC2SPacket {
    public static final Identifier INTERACT_PART_OWNER_PACKET = new Identifier(UselessReptile.MODID, "interact_part_owner");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(INTERACT_PART_OWNER_PACKET, (server, player, handler, buffer, sender) -> {
            URDragonEntity entity = (URDragonEntity) player.getServerWorld().getEntityById(buffer.readInt());
            PlayerEntity playerEntity = (PlayerEntity) player.getServerWorld().getEntityById(buffer.readInt());

            if (entity != null && playerEntity != null) playerEntity.interact(entity, playerEntity.getActiveHand());
        });
    }
}
