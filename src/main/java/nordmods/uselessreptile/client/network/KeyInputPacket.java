package nordmods.uselessreptile.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.network.KeyInputC2SPacket;

@Environment(EnvType.CLIENT)
public class KeyInputPacket {
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null)
                if (player.getVehicle() instanceof URRideableDragonEntity dragon) {
                    boolean jump = dragon.isJumpPressed();
                    boolean forward = dragon.isMoveForwardPressed();
                    boolean back = dragon.isMoveBackPressed();
                    boolean sprint = dragon.isSprintPressed();
                    boolean secondaryAttack = dragon.isSecondaryAttackPressed;
                    boolean primaryAttack = dragon.isPrimaryAttackPressed;
                    boolean down = dragon.isDownPressed();
                    int id = dragon.getId();
                    ClientPlayNetworking.send(new KeyInputC2SPacket(jump, forward, back, sprint, secondaryAttack, primaryAttack, down, id));
                }
        });
    }
}
