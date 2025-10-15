package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.ResourceUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

public class URGlowingLayer<T extends GeoAnimatable & AssetCahceOwner, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    private final Function<R, ? extends AssetCache> assetCahceGetter;
    public URGlowingLayer(software.bernie.geckolib.renderer.base.GeoRenderer<T, O, R> entityRendererIn, Function<R, ? extends AssetCache> assetCahceGetter) {
        super(entityRendererIn);
        this.assetCahceGetter = assetCahceGetter;
    }
    public void submitRenderTask(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState,
                                 int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
        if (URClientConfig.getConfig().disableEmissiveTextures) return;
        if (!ResourceUtil.isResourceReloadFinished) return;

        AssetCache assetCache = assetCahceGetter.apply(renderState);
        if (!assetCache.hasGlowing()) return;
        Identifier id = assetCache.getGlowLayerLocationCache();
        if (id == null) {
            id = getGlowingTexture(renderState);
            if (!ResourceUtil.doesExist(id, false)) {
                assetCache.setHasGlowing(false);
                return;
            }
        }

        RenderLayer renderLayer =  RenderLayer.getEyes(id);//OverlayTexture.DEFAULT_UV
        renderer.buildRenderTask(renderState, poseStack, bakedModel, renderTasks.getBatchingQueue(1), cameraState, renderLayer, LightmapTextureManager.MAX_LIGHT_COORDINATE, packedOverlay, renderColor);
    }

    protected Identifier getGlowingTexture(R state) {
        String namespace = getTextureResource(state).getNamespace();
        String path = getTextureResource(state).getPath().replace(".png", "_glowing.png");
        Identifier id = Identifier.of(namespace, path);
        assetCahceGetter.apply(state).setGlowLayerLocationCache(id);
        return id;
    }
}
