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
import nordmods.uselessreptile.client.model.URDragonModel;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DragonSaddleLayer <T extends URRideableDragonEntity> extends GeoRenderLayer<T> {

    private static Identifier SADDLE;

    public DragonSaddleLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        String dragonName = ((URDragonModel<T>)getGeoModel()).dragonName;
        SADDLE = new Identifier(UselessReptile.MODID, "textures/entity/"+ dragonName +"/saddle.png");
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() != Items.SADDLE) return;
        super.render(matrixStackIn, entity, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        RenderLayer cameo = RenderLayer.getEntityCutout(SADDLE);

        matrixStackIn.push();
        getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
        matrixStackIn.pop();
    }
}
