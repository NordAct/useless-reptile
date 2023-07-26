package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.network.AttackTypeSyncS2CPacket;

public class AttackTypeSyncPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(AttackTypeSyncS2CPacket.ATTACK_TYPE_PACKET, (client, handler, buffer, sender) -> {
            int attackType = buffer.readInt();
            URDragonEntity dragon = (URDragonEntity) client.world.getEntityById(buffer.readInt());
            client.execute(() ->{
                if (dragon != null) dragon.attackType = attackType;
            });
        });
    }
}
