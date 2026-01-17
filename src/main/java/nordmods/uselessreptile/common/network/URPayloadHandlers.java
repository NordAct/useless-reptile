package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.network.c2s.KeyInputPayload;
import nordmods.uselessreptile.common.network.c2s.RequestLiftoffPayload;

public class URPayloadHandlers {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(RequestLiftoffPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URRideableFlyingDragonEntity dragon && !dragon.isFlying() && dragon.hasControllingPassenger() && context.player().getVehicle() == entity) {
                dragon.startToFly();
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(KeyInputPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URRideableDragonEntity dragon && dragon.hasControllingPassenger() && context.player().getVehicle() == entity) {
                dragon.updateInputs(
                        packet.forward(),
                        packet.back(),
                        packet.jump(),
                        packet.down(),
                        packet.secondaryAttack(),
                        packet.primaryAttack(),
                        packet.sprint(),
                        packet.freeLook()
                );
            }
        });
    }
}
