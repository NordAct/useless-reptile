package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.primitive_multipart_entities.common.entity.EntityPart;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.MultipartDragon;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public record SyncEntityPartsPosPayload(int ownerId, List<Vec3> poses)  implements CustomPacketPayload{
    public static final Identifier ID = UselessReptile.id("sync_entity_parts_pos");
    public static final CustomPacketPayload.Type<SyncEntityPartsPosPayload> PAYLOAD_ID = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEntityPartsPosPayload> PACKET_CODEC = StreamCodec.of(
            (byteBuf, payload) -> {
                byteBuf.writeInt(payload.ownerId());
                Vec3.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(byteBuf, payload.poses());
            },
            (byteBuf) -> {
                int id = byteBuf.readInt();
                List<Vec3> poses = Vec3.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(byteBuf);
                return new SyncEntityPartsPosPayload(id, poses);
            });

    public static <T extends Entity & MultipartDragon> void send(ServerPlayer player, T entity) {
        List<Vec3> poses = new ArrayList<>(entity.getParts().length);
        for (EntityPart part : entity.getParts()) poses.add(part.position());
        ServerPlayNetworking.send(player, new SyncEntityPartsPosPayload(entity.getId(), poses));
    }

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
