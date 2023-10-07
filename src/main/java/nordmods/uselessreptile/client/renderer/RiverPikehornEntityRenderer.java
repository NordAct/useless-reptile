package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import nordmods.uselessreptile.client.model.RiverPikehornEntityModel;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;

public class RiverPikehornEntityRenderer extends URDragonRenderer<RiverPikehornEntity> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RiverPikehornEntityModel(), false);
        shadowRadius = 0.4f;
    }

    @Override
    public void render(RiverPikehornEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.getVehicle() instanceof PlayerEntity) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
