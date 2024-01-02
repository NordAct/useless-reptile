package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.init.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.GeoAbstractTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class URGlowingLayer <T extends URDragonEntity> extends AutoGlowingGeoLayer<T> {
    public URGlowingLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }
    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel model, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (!URClientConfig.getConfig().disableEmissiveTextures && MinecraftClient.getInstance().getResourceManager().getResource(getGlowingMask(getTextureResource(getRenderer().getAnimatable()))).isPresent())
            super.render(matrixStackIn, entity, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    private Identifier getGlowingMask(Identifier texture) {
        return GeoAbstractTexture.appendToPath(texture, "_glowmask");
    }
}
