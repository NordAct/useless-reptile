package nordmods.uselessreptile.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.network.LiftoffParticlesS2CPacket;

public class LiftoffParticlesPacket {
    public static<T extends URDragonEntity & FlyingDragon> void init() {
        ClientPlayNetworking.registerGlobalReceiver(LiftoffParticlesS2CPacket.LIFTOFF_PARTICLES_PACKET, (client, handler, buffer, sender) -> {
            if (client.world == null) return;
            T dragon = (T) client.world.getEntityById(buffer.readInt());
            client.execute(() ->{
                if (client.player != null && dragon != null && !dragon.isInsideWaterOrBubbleColumn()) {
                    float span = dragon.getWidthMod()/2;
                    for (int i = 0; i < 25 * span; i++)
                        client.world.addParticle(ParticleTypes.CLOUD,
                                dragon.getX(), dragon.getY() + 1, dragon.getZ(),
                                client.player.getRandom().nextGaussian() * 0.1 * span,
                                -Math.abs(client.player.getRandom().nextGaussian()) * 0.05 * span,
                                client.player.getRandom().nextGaussian() * 0.1 * span);
                }
            });
        });
    }
}
