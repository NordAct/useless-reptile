package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.network.LiftoffParticlesS2CPacket;

public class LiftoffParticlesPacket {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(LiftoffParticlesS2CPacket.PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.id());
            if (entity instanceof URDragonEntity dragon) {
                float span = dragon.getWidthMod()/2;
                for (int i = 0; i < 25 * span; i++)
                    context.player().getWorld().addParticle(ParticleTypes.CLOUD,
                            dragon.getX(), dragon.getY() + 1, dragon.getZ(),
                            context.player().getRandom().nextGaussian() * 0.1 * span,
                            -Math.abs(context.player().getRandom().nextGaussian()) * 0.05 * span,
                            context.player().getRandom().nextGaussian() * 0.1 * span);
            }
        });
    }
}
