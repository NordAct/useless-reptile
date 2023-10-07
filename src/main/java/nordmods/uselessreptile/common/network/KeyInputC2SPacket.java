package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public class KeyInputC2SPacket {
    public static final Identifier KEY_INPUT_PACKET = new Identifier(UselessReptile.MODID, "key_input");
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(KEY_INPUT_PACKET, (server, player, handler, buffer, sender) -> {
            boolean isJumpPressed = buffer.readBoolean();
            boolean isMoveForwardPressed = buffer.readBoolean();
            boolean isMoveBackPressed = buffer.readBoolean();
            boolean isSprintPressed = buffer.readBoolean();
            boolean isSecondaryAttackPressed = buffer.readBoolean();
            boolean isPrimaryAttackPressed = buffer.readBoolean();
            boolean isDownPressed = buffer.readBoolean();
            LivingEntity entity = (LivingEntity) player.getWorld().getEntityById(buffer.readInt());
            if (entity instanceof URRideableDragonEntity dragon) {
                dragon.isSecondaryAttackPressed = isSecondaryAttackPressed;
                dragon.isPrimaryAttackPressed = isPrimaryAttackPressed;

                dragon.updateInputs(isMoveForwardPressed, isMoveBackPressed, isJumpPressed, isDownPressed, isSprintPressed);
            }
        });
    }
}
