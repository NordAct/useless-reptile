package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.WyvernEntityModel;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonRenderer;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class WyvernEntityRenderer extends URRideableDragonRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernEntityModel(), true, false, "rider");
        shadowRadius = 1.5f;
    }

    @Override
    public void preRender(MatrixStack poseStack, WyvernEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        updateSaddle(animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void updateSaddle (WyvernEntity entity) {
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.SADDLE);
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
    }
}
