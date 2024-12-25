package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.AssetCache;
import nordmods.uselessreptile.client.util.AssetCahceOwner;
import nordmods.uselessreptile.client.util.ResourceUtil;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.object.Color;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class URGlowingLayer<T extends GeoAnimatable & AssetCahceOwner> extends GeoRenderLayer<T> {
    public URGlowingLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }
    public void render(MatrixStack matrixStackIn, T animatable, BakedGeoModel model, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (URClientConfig.getConfig().disableEmissiveTextures) return;
        if (!ResourceUtil.isResourceReloadFinished) return;

        AssetCache assetCache = animatable.getAssetCache();
        if (!assetCache.hasGlowing()) return;
        Identifier id = assetCache.getGlowLayerLocationCache();
        if (id == null) {
            id = getGlowingTexture(animatable);
            if (!ResourceUtil.doesExist(id, false)) {
                assetCache.setHasGlowing(false);
                return;
            }
        }

        RenderLayer renderLayer =  RenderLayer.getEyes(id);
        getRenderer().reRender(model, matrixStackIn, bufferSource, animatable, renderLayer,
                bufferSource.getBuffer(renderLayer), partialTick, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                Color.WHITE.getColor());
    }

    protected Identifier getGlowingTexture(T animatable) {
        String namespace = getTextureResource(animatable).getNamespace();
        String path = getTextureResource(animatable).getPath().replace(".png", "_glowing.png");
        Identifier id = Identifier.of(namespace, path);
        animatable.getAssetCache().setGlowLayerLocationCache(id);

        return id;
    }
}
