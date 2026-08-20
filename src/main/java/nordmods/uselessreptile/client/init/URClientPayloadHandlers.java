package nordmods.uselessreptile.client.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.client.gui.URDragonScreen;
import nordmods.uselessreptile.client.gui.VariantChangingOrbScreen;
import nordmods.uselessreptile.common.entity.base.MultipartDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.projectile.LightningBreath;
import nordmods.uselessreptile.common.gui.URDragonMenu;
import nordmods.uselessreptile.common.network.s2c.*;

public class URClientPayloadHandlers {
    public static void init() {
        handleOpenDragonInventory();
        handleLiftoffParticles();
        handleSyncLightningBreathRotations();
        handleOpenVariantChangingOrbScreen();
        handleSyncEntityPartsPosPayload();
        handleSyncBoneTransformsPayload();
    }

    private static void handleOpenDragonInventory() {
        ClientPlayNetworking.registerGlobalReceiver(OpenDragonInventoryPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.dragonId());
            if (entity instanceof URDragonEntity dragon) {
                URDragonMenu dragonMenu = new URDragonMenu(packet.containterId(), context.player().getInventory(), dragon.getInventory());
                context.player().containerMenu = dragonMenu;
                Minecraft.getInstance().setScreen(new URDragonScreen<>(dragonMenu, context.player().getInventory(), dragon));
            }
        });
    }

    private static void handleLiftoffParticles() {
        ClientPlayNetworking.registerGlobalReceiver(LiftoffParticlesPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon) {
                float span = dragon.getBbWidth()/2;
                for (int i = 0; i < 25 * span; i++)
                    context.player().level().addParticle(ParticleTypes.CLOUD,
                            dragon.getX(), dragon.getY() + 1, dragon.getZ(),
                            context.player().getRandom().nextGaussian() * 0.1 * span,
                            -Math.abs(context.player().getRandom().nextGaussian()) * 0.05 * span,
                            context.player().getRandom().nextGaussian() * 0.1 * span);
            }
        });
    }

    private static void handleSyncLightningBreathRotations() {
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

    private static void handleOpenVariantChangingOrbScreen() {
        ClientPlayNetworking.registerGlobalReceiver(OpenVariantChangingOrbScreenPayload.PAYLOAD_ID, (packet, _) -> {
            Minecraft.getInstance().setScreen(new VariantChangingOrbScreen(packet.variantType(), packet.variant()));
        });
    }

    private static void handleSyncEntityPartsPosPayload() {
        ClientPlayNetworking.registerGlobalReceiver(SyncEntityPartsPosPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.ownerId());
            if (entity instanceof MultipartDragon multipartEntity) {
                multipartEntity.handleSyncPayload(packet);
            }
        });
    }

    private static void handleSyncBoneTransformsPayload() {
        ClientPlayNetworking.registerGlobalReceiver(SyncBoneTransformsPayload.PAYLOAD_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id());
            if (entity instanceof URDragonEntity dragon && dragon.getAnimationProcessor() != null) {
                dragon.getAnimationProcessor().handleSyncBoneTransformsPayload(packet);
            }
        });
    }
}
