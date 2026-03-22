package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.item.component.DragonVariantComponent;
import nordmods.uselessreptile.common.network.c2s.*;

public class URPayloadHandlers {
    public static void init() {
        handleRequestLiftoff();
        handleKeyInput();
        handleOrder();
        handleChangeWanderRadius();
        handleUnbindInstrument();
        handleSetVariantChangingOrbVariant();
    }

    private static void handleRequestLiftoff() {
        ServerPlayNetworking.registerGlobalReceiver(RequestLiftoffPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URRideableFlyingDragonEntity dragon && !dragon.isFlying() && dragon.hasControllingPassenger() && context.player().getVehicle() == entity) {
                dragon.startToFly();
            }
        });
    }

    private static void handleKeyInput() {
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

    private static void handleOrder() {
        ServerPlayNetworking.registerGlobalReceiver(OrderPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setCurrentOrder(packet.order());
            }
        });
    }

    private static void handleChangeWanderRadius() {
        ServerPlayNetworking.registerGlobalReceiver(ChangeWanderRadiusPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setWanderRadius(packet.wanderRadius());
            }
        });
    }

    private static void handleUnbindInstrument() {
        ServerPlayNetworking.registerGlobalReceiver(UnbindInstrumentPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.isOwnedBy(context.player())) {
                dragon.setBoundedInstrumentSound("");
            }
        });
    }

    private static void handleSetVariantChangingOrbVariant() {
        ServerPlayNetworking.registerGlobalReceiver(SetVariantChangingOrbVariantPayload.PAYLOAD_ID, (packet, context) -> {
            ServerPlayer player = context.player();
            ItemStack orb = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (orb.getItem() != URItems.VARIANT_CHANGING_ORB) {
                orb = player.getItemInHand(InteractionHand.OFF_HAND);
            }

            if (orb.getItem() != URItems.VARIANT_CHANGING_ORB) return;

            orb.set(URItemComponents.DRAGON_VARIANT, new DragonVariantComponent(packet.variantType(), packet.variant()));
        });
    }
}
