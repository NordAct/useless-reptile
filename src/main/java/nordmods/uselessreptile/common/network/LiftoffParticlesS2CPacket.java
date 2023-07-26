package nordmods.uselessreptile.common.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class LiftoffParticlesS2CPacket {
    public static final Identifier LIFTOFF_PARTICLES_PACKET = new Identifier(UselessReptile.MODID, "liftoff_particles");

    public static <T extends URDragonEntity & FlyingDragon> void  send(ServerPlayerEntity player, T dragon) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(dragon.getId());
        ServerPlayNetworking.send(player, LIFTOFF_PARTICLES_PACKET, buf);
    }
}
