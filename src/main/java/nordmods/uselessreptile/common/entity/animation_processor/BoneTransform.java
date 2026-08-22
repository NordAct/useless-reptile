package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record BoneTransform(Optional<Vector3fc> scale, Optional<Vector3fc> pos, Optional<Vector3fc> rot) {
    public static final Vector3fc ZERO = new Vector3f();
    public static final Vector3fc ONE = new Vector3f(1, 1, 1);
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

    public static @NonNull Map<String, BoneTransform> collectBoneTransforms(List<AnimatedBone> bones) {
        Map<String, BoneTransform> boneTransforms = new HashMap<>();
        for (AnimatedBone bone : bones) {
            AnimatedBone.AnimationPose pose = bone.getAnimationPose();
            Optional<Vector3fc> scale = pose.scale().equals(ONE) ? Optional.empty() : Optional.of(new Vector3f(pose.scale()));
            Optional<Vector3fc> pos = pose.position().equals(ZERO) ? Optional.empty() : Optional.of(new Vector3f(pose.position()));
            Optional<Vector3fc> rot = pose.rotation().equals(ZERO) ? Optional.empty() : Optional.of(new Vector3f(pose.rotation()));

            if (scale.isPresent() || pos.isPresent() || rot.isPresent()) boneTransforms.put(bone.getBone().name(), new BoneTransform(scale, pos, rot));
        }
        return boneTransforms;
    }
}
