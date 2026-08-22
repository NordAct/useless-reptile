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
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SyncBoneTransformsPayload(int id, Map<String, BoneTransform> boneTransforms, Optional<EquipmentSlot> equipmentSlot) implements CustomPacketPayload {
    private static final int MAX_BONES_AMOUNT = 128; //so far, worst offender here is Lightning Chaser with 98 bones and I hope I won't need to expand this further
    public static final Identifier ID = UselessReptile.id("sync_bone_transforms");
    public static final CustomPacketPayload.Type<SyncBoneTransformsPayload> PAYLOAD_ID = new CustomPacketPayload.Type<>(ID);
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, BoneTransform>> MAP_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            BoneTransform.STREAM_CODEC,
            MAX_BONES_AMOUNT
    );
    private static final StreamCodec<ByteBuf, Optional<EquipmentSlot>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, EquipmentSlot.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoneTransformsPayload> STREAM_CODEC = StreamCodec.of(
            ((output, value) -> {
                output.writeInt(value.id);
                MAP_STREAM_CODEC.encode(output, value.boneTransforms);
                OPTIONAL_STREAM_CODEC.encode(output, value.equipmentSlot);
            }),
            (input -> {
                int id = input.readInt();
                Map<String, BoneTransform>  boneTransforms = MAP_STREAM_CODEC.decode(input);
                Optional<EquipmentSlot> equipmentSlot = OPTIONAL_STREAM_CODEC.decode(input);
                return new SyncBoneTransformsPayload(id, boneTransforms, equipmentSlot);
            })
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }

    public static <T extends URDragonEntity> void send(ServerPlayer player, T entity, @Nullable EquipmentSlot equipmentSlot) {
        List<AnimatedBone> bones;
        if (equipmentSlot == null) {
            bones = entity.getAnimationProcessor().getModel().getBones();
        } else {
            DragonEquipment equipment = entity.getAssetCache().getEquipment(equipmentSlot);
            if (equipment == null || equipment.getAnimationProcessor() == null) return;
            bones = equipment.getAnimationProcessor().getModel().getBones();
        }
        Map<String, BoneTransform> boneTransforms = BoneTransform.collectBoneTransforms(bones);
        ServerPlayNetworking.send(player, new SyncBoneTransformsPayload(entity.getId(), boneTransforms, Optional.ofNullable(equipmentSlot)));
    }
}
