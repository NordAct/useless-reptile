package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.network.KeyInputSyncS2CPacket;

public class KeyInputSyncPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(KeyInputSyncS2CPacket.KEY_INPUT_SYNC_PACKET, (client, handler, buffer, sender) -> {
            boolean isJumpPressed = buffer.readBoolean();
            boolean isMoveForwardPressed = buffer.readBoolean();
            boolean isMoveBackPressed = buffer.readBoolean();
            boolean isSprintPressed = buffer.readBoolean();
            boolean isMeleeAttackPressed = buffer.readBoolean();
            boolean isRangeAttackPressed = buffer.readBoolean();
            boolean isDownPressed = buffer.readBoolean();
            URRideableDragonEntity dragon = client.world != null ? (URRideableDragonEntity) client.world.getEntityById(buffer.readInt()) : null;
            client.execute(() ->{
                if (dragon != null && dragon.getPrimaryPassenger() != client.player) {
                    dragon.isSecondaryAttackPressed = isMeleeAttackPressed;
                    dragon.isPrimaryAttackPressed =isRangeAttackPressed;

                    dragon.updateInputs(isMoveForwardPressed, isMoveBackPressed, isJumpPressed, isDownPressed, isSprintPressed);
                }
            });
        });
    }
}
