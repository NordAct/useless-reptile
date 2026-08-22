package nordmods.uselessreptile.common.entity.animation_processor;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.model_provider.DragonEquipmentModelProvider;
import nordmods.uselessreptile.common.init.URStateDataTypes;
import nordmods.uselessreptile.common.network.s2c.SyncEquipmentBoneTransformsPayload;

import java.util.Collection;
import java.util.Map;

public class EquipmentAnimationProcessor extends SyncronizedAnimationProcessor<DragonEquipment, SyncEquipmentBoneTransformsPayload> {
    private static final DragonEquipmentModelProvider MODEL_PROVIDER = new DragonEquipmentModelProvider();
    public EquipmentAnimationProcessor(DragonEquipment animatable) {
        super(animatable);
    }

    @Override
    public boolean isClientSide() {
        return animatable.owner.level().isClientSide();
    }

    @Override
    public void sendSyncPacket() {
        if (animatable.owner.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : PlayerLookup.tracking(serverLevel, animatable.owner.blockPosition()))
                SyncEquipmentBoneTransformsPayload.send(player, animatable);
        }
    }

    @Override
    public void updateBRState() {
        state.setStateData(URStateDataTypes.BONE_TRANSFORMS, animatable.ownerState.getStateData(URStateDataTypes.BONE_TRANSFORMS));
        Collection<BRAnimationController> ownerControllers = animatable.ownerState.getStateData(StateDataTypes.CONTROLLERS);
        ownerControllers.forEach(controller -> {
            controller.getPlayingAnimations().forEach(playingAnimation -> {
                if (playingAnimation.isDone()) return;
                String name = playingAnimation.getAnimation().name();
                if (animatable.controller.getAnimation(name) != null) {
                    animatable.controller.playAnimation(
                            name,
                            playingAnimation.getTransitionInTime(),
                            playingAnimation.getTransitionOutTime(),
                            playingAnimation.getTransitionInLerp(),
                            playingAnimation.getTransitionOutLerp()
                    );
                    return;
                }
                BRPlayingAnimation animation = new BRPlayingAnimation(
                        animatable.controller.getAnimationData(name),
                        playingAnimation.getTransitionInTime(),
                        playingAnimation.getTransitionOutTime(),
                        playingAnimation.getTransitionInLerp(),
                        playingAnimation.getTransitionOutLerp(),
                        playingAnimation.getTransitionInTime() * playingAnimation.getTransitionInLerp().apply(playingAnimation.getTransitionInProgress())
                );
                animation.setAnimationTime(playingAnimation.getAnimationTime());
                animatable.controller.playAnimation(animation);
            });
            animatable.controller.checkAgainstOtherController(controller);
        });
        super.updateBRState();
        state.setStateData(URStateDataTypes.DRAGON_ID, animatable.ownerState.getStateData(URStateDataTypes.DRAGON_ID));
        state.setStateData(URStateDataTypes.ASSET_CACHE, animatable.getAssetCache());
    }

    @Override
    public BRModelProvider getModelProvider() {
        return MODEL_PROVIDER;
    }

    @Override
    public float getAnimationTime() {
        return animatable.ownerState.getStateData(StateDataTypes.ANIMATION_TIME, 0f);
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) {
        super.adjustAnimation(state, model);
        state.getStateData(URStateDataTypes.BONE_TRANSFORMS, Map.of()).forEach((bone, transform) -> {
            AnimatedBone animatedBone = getModel().getBone(bone);
            if (animatedBone == null) return;
            AnimatedBone.AnimationPose pose = animatedBone.getAnimationPose();
            pose.position().add(transform.pos().orElse(BoneTransform.ZERO));
            pose.rotation().add(transform.rot().orElse(BoneTransform.ZERO));
            pose.scale().mul(transform.scale().orElse(BoneTransform.ONE));
        });
    }

    @Override
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, DragonEquipment animatable, float tickDelta) {
        builder.setQuery("body_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_X_ROTATION, 0f));
        builder.setQuery("head_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_X_ROTATION, 0f));
        builder.setQuery("body_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_Y_ROTATION, 0f));
        builder.setQuery("head_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_Y_ROTATION, 0f));
        builder.setQuery("yaw_speed", animatable.ownerState.getStateData(URStateDataTypes.YAW_SPEED, 0f));
    }
}
