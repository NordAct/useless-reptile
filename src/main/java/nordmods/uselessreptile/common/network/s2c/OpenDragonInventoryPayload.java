package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.NotNull;

public record OpenDragonInventoryPayload(int dragonId, int containterId) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("open_dragon_inventory");
    public static final Type<OpenDragonInventoryPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDragonInventoryPayload> PACKET_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.dragonId);
                buf.writeInt(packet.containterId);
            },
            buf -> new OpenDragonInventoryPayload(buf.readInt(), buf.readInt()));

    public static void send(ServerPlayer player, URDragonEntity dragon, int containterId) {
        ServerPlayNetworking.send(player, new OpenDragonInventoryPayload(dragon.getId(), containterId));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
