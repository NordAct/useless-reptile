package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public abstract class HeadMountDragonEntityRenderer<T extends URDragonEntity & HeadMountDragon, R extends LivingEntityRenderState & GeoRenderState>  extends URDragonEntityRenderer<T, R> {
    public HeadMountDragonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityRenderState renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (dragonRenderState.isRidingPlayer) {
            if (HeadMountDragonFeatureRenderer.ON_HEAD.contains(dragonRenderState.UUID)) return;
            else if (dragonRenderState.shouldRenderToClient) return;
        }
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(R renderState, MatrixStack poseStack, float nativeScale) {
        if (renderState.isRidingPlayer) {
            if (renderState.flipUpsideDown) {
                poseStack.translate(0, (renderState.height + 0.1f) / nativeScale, 0);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            }
        } else super.applyRotations(renderState, poseStack, nativeScale);
    }
}
