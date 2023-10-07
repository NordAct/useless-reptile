package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class AttackPartOwnerC2SPacket {
    public static final Identifier ATTACK_PART_OWNER_PACKET = new Identifier(UselessReptile.MODID, "attack_part_owner");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ATTACK_PART_OWNER_PACKET, (server, player, handler, buffer, sender) -> {
            URDragonEntity entity = (URDragonEntity) player.getWorld().getEntityById(buffer.readInt());
            PlayerEntity playerEntity = (PlayerEntity) player.getWorld().getEntityById(buffer.readInt());

            if (entity != null && playerEntity != null) playerEntity.attack(entity);
        });
    }
}
