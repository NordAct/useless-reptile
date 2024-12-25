package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.model.special.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable> {

    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        addRenderLayer(new URGlowingLayer<>(this));
    }

    //have to override that because for some reason they give offset for matrix by 0.5 on each axis
    @Override
    public void preRender(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.objectRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        URDragonEntity owner = animatable.owner;

        if (!isReRender) {
            float yaw = MathHelper.lerpAngleDegrees(partialTick, owner.prevBodyYaw, owner.bodyYaw);
            if (owner.isFrozen())
                yaw += (float) (Math.cos(owner.age * 3.25d) * Math.PI * 0.4d);
            yaw = 180f - yaw;
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

            if (owner.deathTime > 0) {
                float deathRotation = (owner.deathTime + partialTick - 1f) / 20f * 1.6f;
                float roll = Math.min(MathHelper.sqrt(deathRotation), 1) * 90f;
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
            }
        }

        scaleWidth = scaleHeight = owner.getScale();
        scaleModelForRender(scaleWidth, scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
        poseStack.translate(0, 0.01, 0);
    }

    //*screams*
    //@Override
    //public void preApplyRenderLayers(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource,
    //                                 VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    //    poseStack.push();
    //    poseStack.translate(0, -0.01, 0);
    //    super.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    //    poseStack.pop();
    //}
//
    //@Override
    //public void applyRenderLayers(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource,
    //                              VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
    //    poseStack.push();
    //    poseStack.translate(0, -0.01, 0);
    //    super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    //    poseStack.pop();
    //}
}