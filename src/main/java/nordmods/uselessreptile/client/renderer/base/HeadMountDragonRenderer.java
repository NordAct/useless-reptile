package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public abstract class HeadMountDragonRenderer<T extends URDragonEntity & HeadMountDragon>  extends URDragonRenderer<T>{
    public HeadMountDragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntityRenderState entityRenderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (animatable.getVehicle() instanceof PlayerEntity player) {
            if (HeadMountDragonFeatureRenderer.ON_HEAD.contains(animatable.getUuid())) return;
            else if (MinecraftClient.getInstance().player == player && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) return;
        }
        super.render(entityRenderState, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(T animatable, MatrixStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        if (animatable.getVehicle() instanceof PlayerEntity) {
            if (LivingEntityRenderer.shouldFlipUpsideDown(animatable)) {
                poseStack.translate(0, (animatable.getHeight() + 0.1f) / nativeScale, 0);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
            }
        } else super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }
}
