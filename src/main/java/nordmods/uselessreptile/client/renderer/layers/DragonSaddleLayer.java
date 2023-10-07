package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonSaddleLayer <T extends URRideableDragonEntity> extends GeoLayerRenderer<T> {


    public DragonSaddleLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                       T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() != Items.SADDLE) return;
        GeoModel model = getEntityModel().getModel(getEntityModel().getModelResource(entity));
        RenderLayer cameo = RenderLayer.getEntityCutout(new Identifier(UselessReptile.MODID, "textures/entity/"+ entity.getDragonID() +"/saddle.png"));

        matrixStackIn.push();
        getRenderer().render(getEntityModel().getModel(getEntityModel().getModelResource(entity)),entity, partialTicks, cameo,
                matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f,
                1f, 1f);
        matrixStackIn.pop();
    }
}
