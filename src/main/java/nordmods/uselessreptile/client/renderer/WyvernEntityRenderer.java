package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.client.renderer.base.URDragonRenderer;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.init.URTags;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class WyvernEntityRenderer extends URDragonRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    @Override
    public void preRender(MatrixStack poseStack, WyvernEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        updateSaddle(animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    public void updateSaddle (WyvernEntity entity) {
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.BODY).isIn(URTags.WYVERN_SADDLES);
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
    }
}
