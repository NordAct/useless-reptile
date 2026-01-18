package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.network.c2s.*;

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

        ServerPlayNetworking.registerGlobalReceiver(OrderPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setCurrentOrder(packet.order());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ChangeWanderRadiusPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setWanderRadius(packet.wanderRadius());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(UnbindInstrumentPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setBoundedInstrumentSound("");
            }
        });
    }
}
