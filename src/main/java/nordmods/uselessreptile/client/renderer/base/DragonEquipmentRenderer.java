package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.model.special.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.BannerLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable> {
    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        addRenderLayer(new URGlowingLayer<>(this));
        addRenderLayer(new DragonPassengerLayer<>(this, "rider"));
        addRenderLayer(new BannerLayer<>(this));
    }

    public void render(MatrixStack poseStack, DragonEquipmentAnimatable animatable, @Nullable VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType,
                       @Nullable VertexConsumer buffer, int packedLight) {
        this.animatable = animatable;
        if (!ResourceUtil.isResourceReloadFinished) return;
        poseStack.push();
        float partialTick = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        URDragonEntity owner = animatable.owner;
        float yaw = MathHelper.lerpAngleDegrees(partialTick, owner.prevBodyYaw, owner.bodyYaw);
        if (owner.isFrozen())
            yaw += (float)(Math.cos(owner.age * 3.25d) * Math.PI * 0.4d);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - yaw));
        if (owner.deathTime > 0) {
            float deathRotation = (owner.deathTime + partialTick - 1f) / 20f * 1.6f;
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Math.min(MathHelper.sqrt(deathRotation), 1) * 90f));
        }
        defaultRender(poseStack, animatable, bufferSource, renderType, buffer, 0, partialTick, packedLight);
        poseStack.pop();
    }

    //have to override that because for some reason they give offset for matrix by 0.5 on each axis
    @Override
    public void preRender(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.objectRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
        poseStack.translate(0, 0.01, 0);
    }

    //*screams*
    @Override
    public void preApplyRenderLayers(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource,
                                      VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        poseStack.push();
        poseStack.translate(0, -0.01, 0);
        super.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        poseStack.pop();
    }

    @Override
    public void applyRenderLayers(MatrixStack poseStack, DragonEquipmentAnimatable animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource,
                                     VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        poseStack.push();
        poseStack.translate(0, -0.01, 0);
        super.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        poseStack.pop();
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, DragonEquipmentAnimatable animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {
        //TODO: actually investigate tf is this bug (note: happens only if DragonPassengerLayer or BannerLayer are rendered)
        if (buffer instanceof BufferBuilder builder && !builder.building) buffer = bufferSource.getBuffer(renderType);

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
