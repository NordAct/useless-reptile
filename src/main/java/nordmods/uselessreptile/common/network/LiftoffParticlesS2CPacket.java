package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.NotNull;

public record LiftoffParticlesS2CPacket(int id) implements CustomPacketPayload {
    public static final ResourceLocation ID = UselessReptile.id("liftoff_particles");
    public static final Type<LiftoffParticlesS2CPacket> PACKET_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiftoffParticlesS2CPacket> PACKET_CODEC = ByteBufCodecs.INT.map(LiftoffParticlesS2CPacket::new, LiftoffParticlesS2CPacket::id).cast();

    public static <T extends URDragonEntity & FlyingDragon> void send(ServerPlayer player, T dragon) {
        ServerPlayNetworking.send(player, new LiftoffParticlesS2CPacket(dragon.getId()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
