package nordmods.uselessreptile.common.network.s2c;

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

public record LiftoffParticlesPayload(int id) implements CustomPacketPayload {
    public static final ResourceLocation ID = UselessReptile.id("liftoff_particles");
    public static final Type<LiftoffParticlesPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, LiftoffParticlesPayload> PACKET_CODEC = ByteBufCodecs.INT.map(LiftoffParticlesPayload::new, LiftoffParticlesPayload::id).cast();

    public static <T extends URDragonEntity & FlyingDragon> void send(ServerPlayer player, T dragon) {
        ServerPlayNetworking.send(player, new LiftoffParticlesPayload(dragon.getId()));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
