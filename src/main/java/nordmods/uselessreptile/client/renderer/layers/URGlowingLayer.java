package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.ResourceUtil;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Function;

public class URGlowingLayer<T extends GeoAnimatable & AssetCahceOwner, R extends GeoRenderState> extends GeoRenderLayer<T, Void, R> {
    private final Function<R, ? extends AssetCache> assetCahceGetter;
    public URGlowingLayer(software.bernie.geckolib.renderer.base.GeoRenderer<T, Void, R> entityRendererIn, Function<R, ? extends AssetCache> assetCahceGetter) {
        super(entityRendererIn);
        this.assetCahceGetter = assetCahceGetter;
    }
    public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                       int packedLight, int packedOverlay, int renderColor) {
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

        RenderLayer renderLayer =  RenderLayer.getEyes(id);
        getRenderer().reRender(renderState, poseStack, bakedModel, bufferSource, renderLayer,
                bufferSource.getBuffer(renderLayer), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                renderColor);
    }

    protected Identifier getGlowingTexture(R state) {
        String namespace = getTextureResource(state).getNamespace();
        String path = getTextureResource(state).getPath().replace(".png", "_glowing.png");
        Identifier id = Identifier.of(namespace, path);
        assetCahceGetter.apply(state).setGlowLayerLocationCache(id);
        return id;
    }
}
