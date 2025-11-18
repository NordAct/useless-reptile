package nordmods.uselessreptile.client.renderer.layers;

import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.ResourceUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;

public class URGlowingLayer<T extends GeoAnimatable & AssetCahceOwner, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    private final Function<R, ? extends AssetCache> assetCahceGetter;
    private final int renderOrder;
    public URGlowingLayer(software.bernie.geckolib.renderer.base.GeoRenderer<T, O, R> entityRendererIn, Function<R, ? extends AssetCache> assetCahceGetter, int renderOrder) {
        super(entityRendererIn);
        this.assetCahceGetter = assetCahceGetter;
        this.renderOrder = renderOrder;
    }

    @Override
    public void submitRenderTask(R renderState, PoseStack poseStack, BakedGeoModel bakedModel, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                                 int packedLight, int packedOverlay, int renderColor, boolean didRenderModel) {
        if (URClientConfig.getConfig().disableEmissiveTextures) return;
        if (!ResourceUtil.isResourceReloadFinished) return;

        AssetCache assetCache = assetCahceGetter.apply(renderState);
        if (!assetCache.hasGlowing()) return;
        ResourceLocation id = assetCache.getGlowLayerLocationCache();
        if (id == null) {
            id = getGlowingTexture(renderState);
            if (!ResourceUtil.doesExist(id, false)) {
                assetCache.setHasGlowing(false);
                return;
            }
        }

        RenderType renderLayer =  RenderType.eyes(id);
        this.renderer.buildRenderTask(renderState, poseStack, bakedModel, this.renderer.getGeoModel(), renderTasks.order(renderOrder), cameraState, renderLayer, packedLight, packedOverlay, renderColor, null);
    }

    protected ResourceLocation getGlowingTexture(R state) {
        String namespace = getTextureResource(state).getNamespace();
        String path = getTextureResource(state).getPath().replace(".png", "_glowing.png");
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        assetCahceGetter.apply(state).setGlowLayerLocationCache(id);
        return id;
    }
}
