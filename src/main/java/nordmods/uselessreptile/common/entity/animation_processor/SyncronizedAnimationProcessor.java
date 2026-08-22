package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.uselessreptile.common.network.s2c.BoneSyncPayload;

import java.util.HashMap;
import java.util.Map;

//todo figure our why model processing seems to stutter with several dragons
public abstract class SyncronizedAnimationProcessor<T extends BRAnimatedObject, P extends CustomPacketPayload & BoneSyncPayload> extends AnimationProcessor<T>{
    protected Map<String, BoneTransform> nextBoneTransforms;
    protected Map<String, BoneTransform> boneTransforms = new HashMap<>();
    protected Map<String, BoneTransform> prevBoneTransforms = new HashMap<>();
    public SyncronizedAnimationProcessor(T animatable) {
        super(animatable);
    }

    @Override
    public void tick() {
        if (isClientSide()) {
            if (nextBoneTransforms != null) {
                prevBoneTransforms = boneTransforms;
                boneTransforms = nextBoneTransforms;
                nextBoneTransforms = null;
            }
        } else {
            super.tick();
        }
        sendSyncPacket();
    }

    public abstract boolean isClientSide();

    public abstract void sendSyncPacket();

    public void handleSyncBoneTransformsPayload(P payload) {
        if (nextBoneTransforms != null) return;
        nextBoneTransforms = payload.boneTransforms();
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
