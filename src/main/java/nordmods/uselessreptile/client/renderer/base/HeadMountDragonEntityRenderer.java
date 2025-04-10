package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public abstract class HeadMountDragonEntityRenderer<T extends URDragonEntity & HeadMountDragon, R extends LivingEntityRenderState & GeoRenderState> extends URDragonEntityRenderer<T, R> {
    public HeadMountDragonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void defaultRender(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, RenderLayer renderType, VertexConsumer buffer) {
        if (renderState.getGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER)) {
            if (HeadMountDragonFeatureRenderer.ON_HEAD.contains(renderState.getGeckolibData(URDataTickets.DRAGON_UUID))) return;
            else if (renderState.getGeckolibData(URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT)) return;
        }
        super.defaultRender(renderState, poseStack, bufferSource, renderType, buffer);
    }

    @Override
    protected void applyRotations(R renderState, MatrixStack poseStack, float nativeScale) {
        if (renderState.getGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER)) {
            if (renderState.flipUpsideDown) {
                poseStack.translate(0, (renderState.height + 0.1f) / nativeScale, 0);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            }
        } else super.applyRotations(renderState, poseStack, nativeScale);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        super.addRenderData(animatable, relatedObject, renderState);
        renderState.addGeckolibData(URDataTickets.DRAGON_IS_RIDING_PLAYER, animatable.getVehicle() instanceof PlayerEntity);
        renderState.addGeckolibData(URDataTickets.DRAGON_UUID, animatable.getUuid());
        renderState.addGeckolibData(
                URDataTickets.DRAGON_SHOULD_RENDER_TO_CLIENT,
                !(animatable.getVehicle() instanceof PlayerEntity player
                        && player == MinecraftClient.getInstance().player
                        && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()));
    }
}
