package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.client.model.special.DragonEqupmentModel;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.init.URDataTickets;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable> {
    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        //TODO
        //addRenderLayer(new URGlowingLayer<>(this));
    }

    //have to override that because for some reason they give offset for matrix by 0.5 on each axis
    @Override
    public void preRender(GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        if (!(renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE) instanceof LivingEntityRenderState ownerRenderState)) return;

        this.objectRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());

        if (!isReRender) {
            float yaw = ownerRenderState.bodyYaw;
            if (ownerRenderState.shaking)
                yaw += (float) (Math.cos(ownerRenderState.age * 3.25d) * Math.PI * 0.4d);
            yaw = 180f - yaw;
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

            if (ownerRenderState.deathTime > 0) {
                float deathRotation = ownerRenderState.deathTime / 20f * 1.6f;
                float roll = Math.min(MathHelper.sqrt(deathRotation), 1) * 90f;
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
            }
        }

        scaleWidth = scaleHeight = ownerRenderState.baseScale;
        scaleModelForRender(renderState, scaleWidth, scaleHeight, poseStack, model, isReRender);
        poseStack.translate(0, 0.01, 0);
    }

    @Override
    public void addRenderData(DragonEquipmentAnimatable animatable, Void relatedObject, GeoRenderState renderState) {
        renderState.addGeckolibData(URDataTickets.DRAGON_RENDER_STATE, animatable.ownerRenderState);
        renderState.addGeckolibData(URDataTickets.ITEM_ID, Registries.ITEM.getId(animatable.item));
        renderState.addGeckolibData(URDataTickets.ASSET_CACHE, animatable.getAssetCache());
    }
}