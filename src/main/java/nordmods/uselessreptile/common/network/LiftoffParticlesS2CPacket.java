package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public record LiftoffParticlesS2CPacket(int id) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("liftoff_particles");
    public static final CustomPayload.Id<LiftoffParticlesS2CPacket> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, LiftoffParticlesS2CPacket> PACKET_CODEC = PacketCodecs.INTEGER.xmap(LiftoffParticlesS2CPacket::new, LiftoffParticlesS2CPacket::id).cast();

    public static <T extends URDragonEntity & FlyingDragon> void  send(ServerPlayerEntity player, T dragon) {
        ServerPlayNetworking.send(player, new LiftoffParticlesS2CPacket(dragon.getId()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
