package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

public record PositionSyncS2CPacket(double x, double y, double z, float yaw, float pitch, int id) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("position_sync");
    public static final Id<PositionSyncS2CPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, PositionSyncS2CPacket> PACKET_CODEC =
            PacketCodec.ofStatic(PositionSyncS2CPacket::write, PositionSyncS2CPacket::read);

    public static void send(ServerPlayerEntity player, Entity entity) {
        ServerPlayNetworking.send(player, new PositionSyncS2CPacket(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch(), entity.getId()));
    }

    private static PositionSyncS2CPacket read(RegistryByteBuf buffer) {
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        float yaw = buffer.readFloat();
        float pitch = buffer.readFloat();
        int id = buffer.readInt();
        return new PositionSyncS2CPacket(x, y, z, yaw, pitch, id);
    }

    private static void write(RegistryByteBuf buf, PositionSyncS2CPacket packet) {
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
        buf.writeInt(packet.id);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
