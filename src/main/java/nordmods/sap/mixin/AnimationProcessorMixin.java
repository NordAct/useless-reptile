package nordmods.sap.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.math.MathHelper;
import nordmods.sap.SAP;
import nordmods.sap.util.AnimationControllerAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationProcessor;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.math.MolangQueries;
import software.bernie.geckolib.loading.math.value.Variable;

import java.util.Collection;
import java.util.Map;

@Mixin(value = AnimationProcessor.class, remap = false)
public abstract class AnimationProcessorMixin<T extends GeoAnimatable> {

    @Shadow protected abstract void resetBoneTransformationMarkers();

    @Shadow protected abstract boolean isSuspectedCompletedRotation(float lastRotation);

    @Shadow public abstract Collection<GeoBone> getRegisteredBones();

    @Shadow public boolean reloadAnimations;

    @Shadow protected abstract Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots);

    @Shadow @Final private Map<String, GeoBone> bones;

    //there's no way to avoid calling OG data ticket class without doing this
    @Inject(method = "tickAnimation", at = @At("HEAD"), cancellable = true)
    private void redirectEntireFuckingMethod(AnimationState<T> animationState, CallbackInfo ci) {
        if (!SAP.isServerSide()) return;

        AnimatableManager<T> animatableManager = animationState.manager();
        Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(animatableManager.getBoneSnapshotCollection());
        double lerpedAnimationTick = animationState.getData(SAP.ANIMATION_TICKS);

        //filtering out bones which are not needed to reduce load (animation processing is not cheap)
        Collection<String> processableBones = animationState.getData(SAP.PROCESSABLE_BONES);
        Map<String, GeoBone> processable = new Object2ObjectOpenHashMap<>();
        processableBones.forEach(bone -> processable.put(bone, bones.get(bone)));

        for (AnimationController<T> controller : animatableManager.getAnimationControllers().values()) {
            if (this.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            for (Variable variable : ((AnimationControllerAccessor<T>) controller).useless_reptile$getUsedVariables()) { //yes, it has to be done here
                switch (variable.name()) {
                    case MolangQueries.ANIM_TIME -> animationState.queryValues().putIfAbsent(variable, controller.getCurrentAnimationSeconds());
                    //TODO support for other queries
                }
            }

            controller.beginTick(animationState, processable, boneSnapshots, lerpedAnimationTick);

            for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
                GeoBone bone = boneAnimation.bone();
                BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

                AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
                AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
                AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();
                AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
                AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
                AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();
                AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
                AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
                AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();
                EasingType easingType = (EasingType) ((AnimationControllerAccessor)controller).useless_reptile$getOverrideEasingTypeFunction().apply(animationState);

                if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
                    bone.setRotX((float)EasingType.lerpWithOverride(rotXPoint, easingType, animationState) + initialSnapshot.getRotX());
                    bone.setRotY((float)EasingType.lerpWithOverride(rotYPoint, easingType, animationState) + initialSnapshot.getRotY());
                    bone.setRotZ((float)EasingType.lerpWithOverride(rotZPoint, easingType, animationState) + initialSnapshot.getRotZ());
                    snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    snapshot.startRotAnim();
                    bone.markRotationAsChanged();
                }

                if (posXPoint != null && posYPoint != null && posZPoint != null) {
                    bone.setPosX((float)EasingType.lerpWithOverride(posXPoint, easingType, animationState));
                    bone.setPosY((float)EasingType.lerpWithOverride(posYPoint, easingType, animationState));
                    bone.setPosZ((float)EasingType.lerpWithOverride(posZPoint, easingType, animationState));
                    snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                    snapshot.startPosAnim();
                    bone.markPositionAsChanged();
                }

                if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
                    bone.setScaleX((float)EasingType.lerpWithOverride(scaleXPoint, easingType, animationState));
                    bone.setScaleY((float)EasingType.lerpWithOverride(scaleYPoint, easingType, animationState));
                    bone.setScaleZ((float)EasingType.lerpWithOverride(scaleZPoint, easingType, animationState));
                    snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    snapshot.startScaleAnim();
                    bone.markScaleAsChanged();
                }
            }

            controller.finishRenderPass();
        }

        reloadAnimations = false;
        double resetTickLength = animationState.getData(SAP.BONE_RESET_TIME);

        for (GeoBone bone : getRegisteredBones()) {
            if (!bone.hasRotationChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isRotAnimInProgress())
                    saveSnapshot.stopRotAnim(lerpedAnimationTick);

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetRotationTick()) / resetTickLength, 1);
                float initialRotX = initialSnapshot.getRotX();
                float initialRotY = initialSnapshot.getRotY();
                float initialRotZ = initialSnapshot.getRotZ();
                float lastXRot = saveSnapshot.getRotX();
                float lastYRot = saveSnapshot.getRotY();
                float lastZRot = saveSnapshot.getRotZ();

                // Let's capture suspected full-rotations and prevent them from back-lerping
                // Far from perfect, but the best I can think of until I redo the system itself
                if (percentageReset == 0) {
                    if (lastXRot != initialRotX && isSuspectedCompletedRotation(lastXRot)) {
                        lastXRot = initialRotX;
                        percentageReset = 1;
                    }

                    if (lastYRot != initialRotY && isSuspectedCompletedRotation(lastYRot)) {
                        lastYRot = initialRotY;
                        percentageReset = 1;
                    }

                    if (lastZRot != initialRotZ && isSuspectedCompletedRotation(lastZRot)) {
                        lastZRot = initialRotZ;
                        percentageReset = 1;
                    }
                }

                bone.setRotX((float) MathHelper.lerp(percentageReset, lastXRot, initialRotX));
                bone.setRotY((float)MathHelper.lerp(percentageReset, lastYRot, initialRotY));
                bone.setRotZ((float)MathHelper.lerp(percentageReset, lastZRot, initialRotZ));

                if (percentageReset >= 1)
                    saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
            }

            if (!bone.hasPositionChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isPosAnimInProgress())
                    saveSnapshot.stopPosAnim(lerpedAnimationTick);

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

                bone.setPosX((float)MathHelper.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX()));
                bone.setPosY((float)MathHelper.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY()));
                bone.setPosZ((float)MathHelper.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ()));

                if (percentageReset >= 1)
                    saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            }

            if (!bone.hasScaleChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isScaleAnimInProgress())
                    saveSnapshot.stopScaleAnim(lerpedAnimationTick);

                double percentageReset = resetTickLength == 0 ? 1 : Math.min((lerpedAnimationTick - saveSnapshot.getLastResetScaleTick()) / resetTickLength, 1);

                bone.setScaleX((float)MathHelper.lerp(percentageReset, saveSnapshot.getScaleX(), initialSnapshot.getScaleX()));
                bone.setScaleY((float)MathHelper.lerp(percentageReset, saveSnapshot.getScaleY(), initialSnapshot.getScaleY()));
                bone.setScaleZ((float)MathHelper.lerp(percentageReset, saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ()));

                if (percentageReset >= 1)
                    saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
            }
        }

        resetBoneTransformationMarkers();
        animatableManager.finishFirstTick();

        ci.cancel();
    }
}
