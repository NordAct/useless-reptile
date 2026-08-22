package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.util.Mth;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.uselessreptile.common.network.s2c.SyncBoneTransformsPayload;

import java.util.concurrent.ConcurrentHashMap;

public abstract class SyncronizedAnimationProcessor<T extends BRAnimatedObject> extends AnimationProcessor<T>{
    private final ConcurrentHashMap<String, BoneTransform> nextBoneTransforms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BoneTransform> boneTransforms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BoneTransform> prevBoneTransforms = new ConcurrentHashMap<>();
    public SyncronizedAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public void tick() {
        if (isClientSide()) {
            if (!nextBoneTransforms.isEmpty()) {
                prevBoneTransforms.clear();
                prevBoneTransforms.putAll(boneTransforms);
                boneTransforms.clear();
                boneTransforms.putAll(nextBoneTransforms);
                nextBoneTransforms.clear();
            }
        } else {
            super.tick();
        }
        sendSyncPacket();
    }

    public abstract boolean isClientSide();

    public abstract void sendSyncPacket();

    public void handleSyncBoneTransformsPayload(SyncBoneTransformsPayload payload) {
        nextBoneTransforms.putAll(payload.boneTransforms());
    }

    public void syncPose(String bone, AnimatedBone.AnimationPose pose, float tickDelta) {
        BoneTransform prevTransform = prevBoneTransforms.get(bone);
        boolean prev = prevTransform != null;
        BoneTransform transform = boneTransforms.get(bone);
        boolean current = transform != null;
        pose.position().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(BoneTransform.ZERO).x() : 0,
                        current ? transform.pos().orElse(BoneTransform.ZERO).x() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(BoneTransform.ZERO).y() : 0,
                        current ? transform.pos().orElse(BoneTransform.ZERO).y() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.pos().orElse(BoneTransform.ZERO).z() : 0,
                        current ? transform.pos().orElse(BoneTransform.ZERO).z() : 0
                )
        );
        pose.rotation().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(BoneTransform.ZERO).x() : 0,
                        current ? transform.rot().orElse(BoneTransform.ZERO).x() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(BoneTransform.ZERO).y() : 0,
                        current ? transform.rot().orElse(BoneTransform.ZERO).y() : 0
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.rot().orElse(BoneTransform.ZERO).z() : 0,
                        current ? transform.rot().orElse(BoneTransform.ZERO).z() : 0
                )
        );
        pose.scale().set(
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(BoneTransform.ONE).x() : 1,
                        current ? transform.scale().orElse(BoneTransform.ONE).x() : 1
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(BoneTransform.ONE).y() : 1,
                        current ? transform.scale().orElse(BoneTransform.ONE).y() : 1
                ),
                Mth.lerp(
                        tickDelta,
                        prev ? prevTransform.scale().orElse(BoneTransform.ONE).z() : 1,
                        current ? transform.scale().orElse(BoneTransform.ONE).z() : 1
                )
        );
    }
}
