package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import nordmods.uselessreptile.client.model.RiverPikehornEntityModel;
import nordmods.uselessreptile.client.renderer.layers.FishItemLayer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RiverPikehornEntityRenderer extends GeoEntityRenderer<RiverPikehornEntity> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RiverPikehornEntityModel());
        addRenderLayer(new FishItemLayer<>(this));
        shadowRadius = 0.4f;
    }

    @Override
    public void render(RiverPikehornEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.getVehicle() instanceof PlayerEntity) return;
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
