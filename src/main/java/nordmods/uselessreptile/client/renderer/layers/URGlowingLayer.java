package nordmods.uselessreptile.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.renderer.layer.TextureRenderLayer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.ResourceUtil;

public class URGlowingLayer extends TextureRenderLayer {
    public URGlowingLayer(BRRenderer<?> parentRenderer, int order) {
        super(parentRenderer, order);
    }

    protected Identifier getGlowingTexture(BRState state) {
        String namespace = parentRenderer.getModelProvider().getModelId(state).getNamespace();
        String path = parentRenderer.getTextureId(state).getPath().replace(".png", "_glowing.png");
        Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
        state.getStateData(URStateDataTypes.ASSET_CACHE).setGlowLayerLocationCache(id);
        return id;
    }

    protected void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (URClientConfig.getConfig().disableEmissiveTextures) return;
        if (!ResourceUtil.isResourceReloadFinished) return;
        if (!state.getStateData(URStateDataTypes.ASSET_CACHE).hasGlowing()) return;

        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return state.getStateData(URStateDataTypes.ASSET_CACHE).getGlowLayerLocationCache();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.eyes(texture);
    }

    @Override
    protected void updateRenderState(BRState state) {
        state.setStateData(ClientStateDataTypes.LIGHT, LightTexture.FULL_BRIGHT);
        state.setStateData(ClientStateDataTypes.INVISIBLE, false);
        AssetCache assetCache = state.getStateData(URStateDataTypes.ASSET_CACHE);
        if (assetCache.hasGlowing() && assetCache.getGlowLayerLocationCache() == null) {
            Identifier id = getGlowingTexture(state);
            if (!ResourceUtil.doesExist(id, false)) {
                assetCache.setHasGlowing(false);
            }
        }
    }
}
