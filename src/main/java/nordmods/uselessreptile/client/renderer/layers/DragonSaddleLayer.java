package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.model.URDragonModel;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.client.util.model_redirect.ModelRedirectUtil;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DragonSaddleLayer <T extends URRideableDragonEntity> extends GeoRenderLayer<T> {


    public DragonSaddleLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (!ResourceUtil.isResourceReloadFinished) {
            entity.setSaddleTextureLocationCache(null);
            return;
        }

        if (!entity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.SADDLE)) return;
        if (entity.getSaddleTextureLocationCache() != null) {
            renderSaddle(entity.getSaddleTextureLocationCache(), matrixStackIn, bufferSource, packedLight, entity, partialTick);
            return;
        }

        Identifier id;
        if (!URClientConfig.getConfig().disableNamedEntityModels && entity.getCustomName() != null) {
            id = ModelRedirectUtil.getCustomSaddleTexturePath(entity, entity.getDragonID());
            if (ResourceUtil.doesExist(id)) {
                entity.setSaddleTextureLocationCache(id);
                renderSaddle(id, matrixStackIn, bufferSource, packedLight, entity, partialTick);
                return;
            }
        }

        id = ModelRedirectUtil.getVariantSaddleTexturePath(entity, entity.getDragonID());
        if (ResourceUtil.doesExist(id)) {
            entity.setSaddleTextureLocationCache(id);
            renderSaddle(id, matrixStackIn, bufferSource, packedLight, entity, partialTick);
            return;
        }

        entity.setSaddleTextureLocationCache(getDefaultSaddleTexture());
        renderSaddle(getDefaultSaddleTexture(), matrixStackIn, bufferSource, packedLight, entity, partialTick);
    }

    private void renderSaddle(Identifier saddle, MatrixStack matrixStackIn, VertexConsumerProvider bufferSource, int packedLightIn, T entity, float partialTick) {
        RenderLayer cameo = RenderLayer.getEntityCutout(saddle);
        getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLightIn, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
    }

    private Identifier getDefaultSaddleTexture() {
        return new Identifier(UselessReptile.MODID, "textures/entity/" + ((URDragonModel<T>)getGeoModel()).getDragonID() + "/saddle.png");

    }
}
