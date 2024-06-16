package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;

public record PosSyncS2CPacket(Vec3d pos, int id) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("pos_sync_packet");
    public static final CustomPayload.Id<PosSyncS2CPacket> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, PosSyncS2CPacket> PACKET_CODEC = PacketCodec.ofStatic(PosSyncS2CPacket::write, PosSyncS2CPacket::read);


    public static void send(ServerPlayerEntity player, URRideableDragonEntity dragon) {
        if (dragon.canBeControlledByRider() && player != dragon.getControllingPassenger()) {
            ServerPlayNetworking.send(player, new PosSyncS2CPacket(dragon.getPos(), dragon.getId()));
        }
    }

    private static PosSyncS2CPacket read(RegistryByteBuf buffer) {
        Vec3d vec3d = buffer.readVec3d();
        int id = buffer.readInt();
        return new PosSyncS2CPacket(vec3d, id);
    }

    private static void write(RegistryByteBuf buf, PosSyncS2CPacket packet) {
        buf.writeVec3d(packet.pos);
        buf.writeInt(packet.id);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
