package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.client.renderer.layers.equipment.DragonSaddleLayer;
import nordmods.uselessreptile.client.renderer.layers.equipment.URDragonEquipmentLayer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public abstract class URRideableDragonRenderer<T extends URRideableDragonEntity> extends URDragonRenderer<T> {
    protected final String riderBone;
    public URRideableDragonRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model, boolean hasBanner, boolean hasArmor, String riderBone) {
        super(renderManager, model, hasBanner, hasArmor);
        this.riderBone = riderBone;
        addRenderLayer(new DragonPassengerLayer<>(this, riderBone));
        addRenderLayer(new DragonSaddleLayer<>(this));
    }

    @Override
    public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        updateSaddle(animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public abstract void updateSaddle(T entity);
}
