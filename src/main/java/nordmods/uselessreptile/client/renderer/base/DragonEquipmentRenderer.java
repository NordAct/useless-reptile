package nordmods.uselessreptile.client.renderer.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRObjectRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.DragonEquipmentModelProvider;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.DragonEquipment;
import nordmods.uselessreptile.client.util.EquipmentAssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

public class DragonEquipmentRenderer extends BRObjectRenderer<DragonEquipment, BRState.Impl> {
    public static final Identifier DEFAULT_TEXTURE = TextureAtlas.LOCATION_BLOCKS;
    public DragonEquipmentRenderer() {
        super(new DragonEquipmentModelProvider());
        addRenderLayer(new URGlowingLayer(this, 2));
    }

    @Override //note to self: DO NOT USE ARMOR RENDER TYPES (it breaks glow layer because armor render type has extra z offset... despite render order existing, duh)
    public RenderType getRenderType(BRState renderState, Identifier texture) {
        if (!ResourceUtil.isResourceReloadFinished) return RenderTypes.armorCutoutNoCull(texture);
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
        state.setStateData(URStateDataTypes.DRAGON_ID, animatable.ownerRenderState.getStateData(URStateDataTypes.DRAGON_ID));
        state.setStateData(URStateDataTypes.ASSET_CACHE, animatable.getAssetCache());
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, animatable.ownerRenderState.getStateData(ClientStateDataTypes.OUTLINE_COLOR));
        state.setStateData(ClientStateDataTypes.LIGHT, animatable.ownerRenderState.getStateData(ClientStateDataTypes.LIGHT));

        Collection<BRAnimationController> ownerControllers = animatable.ownerRenderState.getStateData(StateDataTypes.CONTROLLERS);
        animatable.cloneController.copyFrom(ownerControllers);
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
                animatable.controller.playAnimation(
                        new BRPlayingAnimation(
                                animatable.controller.getAnimationData(name),
                                playingAnimation.getTransitionInTime(),
                                playingAnimation.getTransitionOutTime(),
                                playingAnimation.getTransitionInLerp(),
                                playingAnimation.getTransitionOutLerp(),
                                playingAnimation.getAnimationTime(),
                                playingAnimation.getTransitionInTime() * playingAnimation.getTransitionInLerp().apply(playingAnimation.getTransitionInProgress())
                        )
                );
            });
            animatable.controller.checkAgainstOtherController(controller);
        });
        state.setStateData(StateDataTypes.CONTROLLERS, animatable.getAnimationControllers());

        state.setStateData(StateDataTypes.ANIMATION_TIME, animatable.ownerRenderState.getStateData(StateDataTypes.ANIMATION_TIME));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, this.getModelProvider());
        state.setStateData(StateDataTypes.SCALE, animatable.ownerRenderState.scale);
    }

    @Override
    public BRState.Impl createRenderState() {
        return new BRState.Impl(new HashMap<>());
    }

    @Override
    public void submitBRModel(BRState.Impl state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!((EquipmentAssetCache)state.getStateData(URStateDataTypes.ASSET_CACHE)).canRender()) return;
        super.submitBRModel(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public void submitBRModelOrdered(BRState.Impl state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, int order) {
        if (!((EquipmentAssetCache)state.getStateData(URStateDataTypes.ASSET_CACHE)).canRender()) return;
        super.submitBRModelOrdered(state, poseStack, submitNodeCollector, cameraRenderState, order);
    }

    @Override
    public DragonEquipmentModelProvider getModelProvider() {
        return (DragonEquipmentModelProvider) super.getModelProvider();
    }
}