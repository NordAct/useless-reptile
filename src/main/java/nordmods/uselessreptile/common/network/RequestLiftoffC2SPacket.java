package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableFlyingDragonEntity;

public record RequestLiftoffC2SPacket(int id) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("request_liftoff");
    public static final Id<RequestLiftoffC2SPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, RequestLiftoffC2SPacket> PACKET_CODEC =
            PacketCodec.ofStatic(RequestLiftoffC2SPacket::write, RequestLiftoffC2SPacket::read);

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (packet, context) -> {
            Entity entity = context.player().getWorld().getEntityById(packet.id);
            if (entity instanceof URRideableFlyingDragonEntity dragon && !dragon.isFlying() && dragon.canBeControlledByRider() && context.player().getVehicle() == entity) {
                dragon.startToFly();
            }
        });
    }

    private static RequestLiftoffC2SPacket read(RegistryByteBuf buffer) {
        int id = buffer.readInt();
        return new RequestLiftoffC2SPacket(id);
    }

    private static void write(RegistryByteBuf buf, RequestLiftoffC2SPacket packet) {
        buf.writeInt(packet.id);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
