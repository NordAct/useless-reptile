package nordmods.uselessreptile.common.network.s2c;

import io.netty.buffer.ByteBuf;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.animation_processor.BoneTransform;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SyncEquipmentBoneTransformsPayload(int ownerId, Map<String, BoneTransform> boneTransforms, EquipmentSlot equipmentSlot) implements CustomPacketPayload, BoneSyncPayload {
    private static final int MAX_BONES_AMOUNT = 128;
    public static final Identifier ID = UselessReptile.id("sync_equipment_bone_transforms");
    public static final Type<SyncEquipmentBoneTransformsPayload> PAYLOAD_ID = new Type<>(ID);
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, BoneTransform>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            BoneTransform.STREAM_CODEC,
            MAX_BONES_AMOUNT
    );
    private static final StreamCodec<ByteBuf, EquipmentSlot> EQUIPMENT_SLOT_STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, EquipmentSlot.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEquipmentBoneTransformsPayload> STREAM_CODEC = StreamCodec.of(
            ((output, value) -> {
                output.writeInt(value.ownerId);
                MAP_STREAM_CODEC.encode(output, value.boneTransforms);
                EQUIPMENT_SLOT_STREAM_CODEC.encode(output, value.equipmentSlot);
            }),
            (input -> {
                int id = input.readInt();
                Map<String, BoneTransform>  boneTransforms = MAP_STREAM_CODEC.decode(input);
                EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_STREAM_CODEC.decode(input);
                return new SyncEquipmentBoneTransformsPayload(id, boneTransforms, equipmentSlot);
            })
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }

    public static void send(ServerPlayer player, DragonEquipment equipment) {
        List<AnimatedBone> bones = equipment.getAnimationProcessor().getModel().getBones();
        Map<String, BoneTransform> boneTransforms = BoneTransform.collectBoneTransforms(bones);
        ServerPlayNetworking.send(player, new SyncEquipmentBoneTransformsPayload(equipment.owner.getId(), boneTransforms, equipment.equipmentSlot));
    }
}
