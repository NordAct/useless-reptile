package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.animation_processor.ControllerState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record SyncAnimationsPayload(int ownerId, List<ControllerState> controllerStates) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("sync_animations");
    public static final Type<SyncAnimationsPayload> PAYLOAD_ID = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAnimationsPayload> PACKET_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeInt(packet.ownerId);
                ControllerState.LIST_STREAM_CODEC.encode(buf, packet.controllerStates);
            },
            buf -> new SyncAnimationsPayload(buf.readInt(), ControllerState.LIST_STREAM_CODEC.decode(buf))
    );

    public static void send(URDragonEntity dragon) {
        if (dragon.level() instanceof ServerLevel serverLevel) {
            SyncAnimationsPayload payload = new SyncAnimationsPayload(dragon.getId(), ControllerState.collectControllerStates(dragon.getAnimationControllers()));
            for (ServerPlayer player : PlayerLookup.tracking(serverLevel, dragon.blockPosition())) send(player, payload);
        }
    }

    public static void send(ServerPlayer player, SyncAnimationsPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }
}
