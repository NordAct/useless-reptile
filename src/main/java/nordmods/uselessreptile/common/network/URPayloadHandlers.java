package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import nordmods.uselessreptile.common.entity.projectile.LightningBreath;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.network.c2s.KeyInputPayload;
import nordmods.uselessreptile.common.network.c2s.RequestLiftoffPayload;
import nordmods.uselessreptile.common.network.s2c.OpenDragonInventoryPayload;
import nordmods.uselessreptile.common.network.s2c.LiftoffParticlesPayload;
import nordmods.uselessreptile.common.network.s2c.SyncLightningBreathRotationsPayload;

public class URPayloadHandlers {
    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(OpenDragonInventoryPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.dragonId());
            if (entity instanceof URDragonEntity dragon) {
                URDragonMenu dragonMenu = new URDragonMenu(packet.containterId(), context.player().getInventory(), dragon.getInventory());
                context.player().containerMenu = dragonMenu;
                Minecraft.getInstance().setScreen(new URDragonScreen<>(dragonMenu, context.player().getInventory(), dragon));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(LiftoffParticlesPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon) {
                float span = dragon.getWidthMod()/2;
                for (int i = 0; i < 25 * span; i++)
                    context.player().level().addParticle(ParticleTypes.CLOUD,
                            dragon.getX(), dragon.getY() + 1, dragon.getZ(),
                            context.player().getRandom().nextGaussian() * 0.1 * span,
                            -Math.abs(context.player().getRandom().nextGaussian()) * 0.05 * span,
                            context.player().getRandom().nextGaussian() * 0.1 * span);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncLightningBreathRotationsPayload.PAYLOAD_ID, (packet, context) -> {
            for (int id : packet.beamIDs()) {
                Entity entity = context.player().level().getEntity(id);
                if (!(entity instanceof LightningBreath lightningBreathEntity)) continue;
                if (id == packet.beamIDs()[0]) lightningBreathEntity.setBeamLength(packet.beamIDs().length);
                entity.setXRot(packet.pitch());
                entity.setYRot(packet.yaw());
            }
        });
    }

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
