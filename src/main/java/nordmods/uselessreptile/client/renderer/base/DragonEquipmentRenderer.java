package nordmods.uselessreptile.client.renderer.base;

import libs.gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRObjectRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.client.util.TextureAtlasSpriteUtil;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.init.URAtlases;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.asset_cache.AssetCache;
import nordmods.uselessreptile.common.entity.animation_processor.BoneTransform;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.model_provider.DragonEquipmentModelProvider;
import nordmods.uselessreptile.common.init.URStateDataTypes;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DragonEquipmentRenderer extends BRObjectRenderer<DragonEquipment, BRState.Impl> {
    public static final Identifier DEFAULT_TEXTURE = TextureAtlas.LOCATION_BLOCKS;
    public DragonEquipmentRenderer() {
        super(new DragonEquipmentModelProvider());
        addRenderLayer(new URGlowingLayer(this, 2));
    }

    @Override //note to self: DO NOT USE ARMOR RENDER TYPES (it breaks glow layer because armor render type has extra z offset... despite render wanderRadius existing, duh)
    public RenderType getRenderType(BRState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderTypes.entityCutout(texture);
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getRenderTypeProviderCache().getRenderType(renderState, texture);
    }

    @Override
    @Nullable
    public Identifier getTextureId(BRState renderState) {
        if (!ResourceUtil.isResourceReloadFinished) return DEFAULT_TEXTURE;
        AssetCache assetCache = renderState.getStateData(URStateDataTypes.ASSET_CACHE);
        return assetCache.getTextureLocationCache();
    }

    @Override
    public void extractRenderState(DragonEquipment animatable, BRState.Impl state, float tickDelta) {
        animatable.getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), animatable, tickDelta));
        state.setStateData(URStateDataTypes.DRAGON_ID, animatable.ownerState.getStateData(URStateDataTypes.DRAGON_ID));
        state.setStateData(URStateDataTypes.ASSET_CACHE, animatable.getAssetCache());
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, animatable.ownerState.getStateData(ClientStateDataTypes.OUTLINE_COLOR));
        state.setStateData(ClientStateDataTypes.LIGHT, animatable.ownerState.getStateData(ClientStateDataTypes.LIGHT));
        state.setStateData(URStateDataTypes.BONE_TRANSFORMS, animatable.ownerState.getStateData(URStateDataTypes.BONE_TRANSFORMS));
        state.setStateData(StateDataTypes.TICK_DELTA, tickDelta);
        Collection<BRAnimationController> ownerControllers = animatable.ownerState.getStateData(StateDataTypes.CONTROLLERS);
        ownerControllers.forEach(controller -> { //todo move to equipment object
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
        state.setStateData(StateDataTypes.CONTROLLERS, animatable.getAnimationControllers());

        state.setStateData(StateDataTypes.ANIMATION_TIME, animatable.ownerState.getStateData(StateDataTypes.ANIMATION_TIME));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, this.getModelProvider());
    }

    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, DragonEquipment animatable, float tickDelta) {
        builder.setQuery("body_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_X_ROTATION, 0f));
        builder.setQuery("head_x_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_X_ROTATION, 0f));
        builder.setQuery("body_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.BODY_Y_ROTATION, 0f));
        builder.setQuery("head_y_rotation", animatable.ownerState.getStateData(URStateDataTypes.HEAD_Y_ROTATION, 0f));
        builder.setQuery("yaw_speed", animatable.ownerState.getStateData(URStateDataTypes.YAW_SPEED, 0f));
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) {
        super.adjustAnimation(state, model);
        if (!ResourceUtil.isResourceReloadFinished) return;
        model.getRootBones().forEach(bone -> bone.setVisible(true));

        state.getStateData(URStateDataTypes.BONE_TRANSFORMS, Map.of()).forEach((bone, transform) -> {
            AnimatedBone animatedBone = model.getBone(bone);
            if (animatedBone == null) return;
            AnimatedBone.AnimationPose pose = animatedBone.getAnimationPose();
            pose.position().add(transform.pos().orElse(BoneTransform.ZERO));
            pose.rotation().add(transform.rot().orElse(BoneTransform.ZERO));
            pose.scale().mul(transform.scale().orElse(BoneTransform.ONE));
        });
    }

    @Override
    public BRState.Impl createRenderState() {
        return new BRState.Impl(new HashMap<>());
    }

    @Override
    public DragonEquipmentModelProvider getModelProvider() {
        return (DragonEquipmentModelProvider) super.getModelProvider();
    }

    @Override
    public @Nullable TextureAtlasSprite getSpriteForTexture(Identifier texture) {
        return TextureAtlasSpriteUtil.getTextureAtlasSprite(URAtlases.ANIMATED_TEXTURES_ATLAS_MAPPER, texture);
    }
}