package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.renderer.layers.DragonSaddleLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
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
    public void render(T animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource,
                       int packedLight) {
        updateSaddle(animatable);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public abstract void updateSaddle(T entity);
}
