package nordmods.uselessreptile.common.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.base.URMultipartEntity;

public record SyncPartPosS2CPacket(int ownerId, Vec3d[] relativePos) implements CustomPayload {
    public static final Identifier ID = UselessReptile.id("sync_part_pos");
    public static final Id<SyncPartPosS2CPacket> PACKET_ID = new Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, SyncPartPosS2CPacket> PACKET_CODEC = PacketCodec.ofStatic(SyncPartPosS2CPacket::write, SyncPartPosS2CPacket::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    private static SyncPartPosS2CPacket read(RegistryByteBuf buffer) {
        int amount = buffer.readInt();
        int ownerId = buffer.readInt();
        Vec3d[] pos = new Vec3d[amount];
        for (int i = 0; i < amount; i++) pos[i] = buffer.readVec3d();
        return new SyncPartPosS2CPacket(ownerId, pos);
    }

    private static void write(RegistryByteBuf buf, SyncPartPosS2CPacket packet) {
        buf.writeInt(packet.relativePos.length);
        buf.writeInt(packet.ownerId);
        for (Vec3d pos : packet.relativePos) buf.writeVec3d(pos);
    }

    public static <T extends URDragonEntity & URMultipartEntity>  void send(ServerPlayerEntity player, T entity, Vec3d[] relativePos) {
        ServerPlayNetworking.send(player, new SyncPartPosS2CPacket(entity.getId(), relativePos));
    }

}
