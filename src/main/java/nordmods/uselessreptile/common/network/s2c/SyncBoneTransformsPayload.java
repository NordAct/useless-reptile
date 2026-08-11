package nordmods.uselessreptile.common.network.s2c;

import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record SyncBoneTransformsPayload(int id, Map<String, BoneTransform> boneTransforms) implements CustomPacketPayload {
    private static final int MAX_BONES_AMOUNT = 128; //so far, worst offender here is Lightning Chaser with 98 bones and I hope I won't need to expand this further
    public static final Identifier ID = UselessReptile.id("sync_bone_transforms");
    public static final CustomPacketPayload.Type<SyncBoneTransformsPayload> PAYLOAD_ID = new CustomPacketPayload.Type<>(ID);
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, BoneTransform>> LIST_STREAM_CODEC = ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            BoneTransform.STREAM_CODEC,
            MAX_BONES_AMOUNT
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBoneTransformsPayload> STREAM_CODEC = StreamCodec.of(
            ((output, value) -> {
                output.writeInt(value.id);
                LIST_STREAM_CODEC.encode(output, value.boneTransforms);
            }),
            (input -> {
                int id = input.readInt();
                Map<String, BoneTransform>  boneTransforms = LIST_STREAM_CODEC.decode(input);
                return new SyncBoneTransformsPayload(id, boneTransforms);
            })
    );
    public static final Vector3fc ZERO = new Vector3f();
    public static final Vector3fc ONE = new Vector3f(1, 1, 1);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return PAYLOAD_ID;
    }

    public static <T extends URDragonEntity> void send(ServerPlayer player, T entity) {
        Map<String, BoneTransform> boneTransforms = new HashMap<>();
        for (AnimatedBone bone : entity.getAnimationProcessor().getModel().getBones()) {
            AnimatedBone.AnimationPose pose = bone.getAnimationPose();
            Optional<Vector3fc> scale = pose.scale().equals(ONE) ? Optional.empty() : Optional.of(pose.scale());
            Optional<Vector3fc> pos = pose.position().equals(ZERO) ? Optional.empty() : Optional.of(pose.position());
            Optional<Vector3fc> rot = pose.rotation().equals(ZERO) ? Optional.empty() : Optional.of(pose.rotation());

            if (scale.isPresent() || pos.isPresent() || rot.isPresent()) boneTransforms.put(bone.getBone().name(), new BoneTransform(scale, pos, rot));
        }
        ServerPlayNetworking.send(player, new SyncBoneTransformsPayload(entity.getId(), boneTransforms));
    }

    public record BoneTransform(Optional<Vector3fc> scale, Optional<Vector3fc> pos, Optional<Vector3fc> rot) {
        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<Vector3fc>> VECTOR3FC = ByteBufCodecs.optional(ByteBufCodecs.VECTOR3F);
        public static final StreamCodec<RegistryFriendlyByteBuf, BoneTransform> STREAM_CODEC = StreamCodec.of(
                ((output, value) -> {
                    VECTOR3FC.encode(output, value.scale);
                    VECTOR3FC.encode(output, value.pos);
                    VECTOR3FC.encode(output, value.rot);
                }),
                (input -> {
                    Optional<Vector3fc> scale = VECTOR3FC.decode(input);
                    Optional<Vector3fc> pos = VECTOR3FC.decode(input);
                    Optional<Vector3fc> rot = VECTOR3FC.decode(input);
                    return new BoneTransform(scale, pos, rot);
                })
        );
    }
}
