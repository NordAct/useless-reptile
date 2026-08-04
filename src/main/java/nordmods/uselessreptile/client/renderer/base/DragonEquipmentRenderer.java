package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRObjectRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.client.util.TextureAtlasSpriteUtil;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.init.URAtlases;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.model_provider.DragonEquipmentModelProvider;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.asset_cache.AssetCache;
import nordmods.uselessreptile.client.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.client.util.ResourceUtil;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;

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

        state.setStateData(StateDataTypes.ANIMATION_TIME, animatable.ownerRenderState.getStateData(StateDataTypes.ANIMATION_TIME));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, this.getModelProvider());
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