package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;
import org.jetbrains.annotations.NotNull;

public record RequestLiftoffC2SPacket(int id) implements CustomPacketPayload {
    public static final ResourceLocation ID = UselessReptile.id("request_liftoff");
    public static final Type<RequestLiftoffC2SPacket> PACKET_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestLiftoffC2SPacket> PACKET_CODEC =
            StreamCodec.of(RequestLiftoffC2SPacket::write, RequestLiftoffC2SPacket::read);

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (packet, context) -> {
            Entity entity = context.player().level().getEntity(packet.id);
            if (entity instanceof URRideableFlyingDragonEntity dragon && !dragon.isFlying() && dragon.canBeControlledByRider() && context.player().getVehicle() == entity) {
                dragon.startToFly();
            }
        });
    }

    private static RequestLiftoffC2SPacket read(RegistryFriendlyByteBuf buffer) {
        int id = buffer.readInt();
        return new RequestLiftoffC2SPacket(id);
    }

    private static void write(RegistryFriendlyByteBuf buf, RequestLiftoffC2SPacket packet) {
        buf.writeInt(packet.id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
