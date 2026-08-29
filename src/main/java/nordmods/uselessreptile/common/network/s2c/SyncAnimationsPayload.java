package nordmods.uselessreptile.common.network.s2c;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.animation_processor.ControllerState;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncAnimationsPayload(int ownerId, List<ControllerState> controllerStates, Map<EquipmentSlot, List<ControllerState>> equipmentControllerStates) implements CustomPacketPayload {
    public static final Identifier ID = UselessReptile.id("sync_animations");
    public static final Type<SyncAnimationsPayload> PAYLOAD_ID = new Type<>(ID);
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<EquipmentSlot, List<ControllerState>>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            EquipmentSlot.STREAM_CODEC,
            ControllerState.LIST_STREAM_CODEC,
            2
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAnimationsPayload> PACKET_CODEC = new StreamCodec<>() {
        @Override
        public SyncAnimationsPayload decode(RegistryFriendlyByteBuf input) {
            return new SyncAnimationsPayload(
                    input.readInt(),
                    ControllerState.LIST_STREAM_CODEC.decode(input),
                    MAP_STREAM_CODEC.decode(input)
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf output, SyncAnimationsPayload value) {
            output.writeInt(value.ownerId);
            ControllerState.LIST_STREAM_CODEC.encode(output, value.controllerStates);
            MAP_STREAM_CODEC.encode(output, value.equipmentControllerStates);
        }
    };

    public static void send(URDragonEntity dragon) {
        if (dragon.level() instanceof ServerLevel serverLevel) {
            Map<EquipmentSlot, List<ControllerState>> equipmentControllerStates = new HashMap<>();
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                DragonEquipment equipment = dragon.getAssetCache().getEquipment(equipmentSlot);
                if (equipment != null && equipment.getAnimationProcessor() != null) equipmentControllerStates.put(equipmentSlot, ControllerState.collectControllerStates(equipment.getAnimationControllers()));
            }
            SyncAnimationsPayload payload = new SyncAnimationsPayload(dragon.getId(), ControllerState.collectControllerStates(dragon.getAnimationControllers()), equipmentControllerStates);
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
