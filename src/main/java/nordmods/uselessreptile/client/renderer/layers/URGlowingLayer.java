package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class URGlowingLayer <T extends URDragonEntity> extends GeoRenderLayer<T> {
    public URGlowingLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }
    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel model, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (URClientConfig.getConfig().disableEmissiveTextures) return;

        if (!ResourceUtil.isResourceReloadFinished) {
            entity.getAssetCache().setGlowLayerLocationCache(null);
            return;
        }

        Identifier id = getGlowingMask(entity);
        if (!ResourceUtil.doesExist(id)) return;

        RenderLayer cameo =  RenderLayer.getEyes(id);
        getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
    }

    private Identifier getGlowingMask(T entity) {
        if (entity.getAssetCache().getGlowLayerLocationCache() != null) return entity.getAssetCache().getGlowLayerLocationCache();

        String namespace = getTextureResource(entity).getNamespace();
        String path = getTextureResource(entity).getPath().replace(".png", "_glowing.png");
        Identifier id = new Identifier(namespace, path);
        entity.getAssetCache().setGlowLayerLocationCache(id);

        return id;
    }
}
