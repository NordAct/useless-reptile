package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.renderer.layers.DragonRiderLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonSaddleLayer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public abstract class URRideableDragonRenderer<T extends URRideableDragonEntity> extends URDragonRenderer<T> {
    protected final float riderOffsetX;
    protected final float riderOffsetY;
    protected final float riderOffsetZ;
    protected final double defaultRiderPitch;
    protected final String[] ignoredBones;
    protected final String riderBone;
    public URRideableDragonRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> model, boolean hasBanner, boolean separatedSaddleLayer, float riderOffsetX, float riderOffsetY, float riderOffsetZ, double defaultRiderPitch, String[] ignoredBones, String riderBone) {
        super(renderManager, model, hasBanner);
        this.riderOffsetX = riderOffsetX;
        this.riderOffsetY = riderOffsetY;
        this.riderOffsetZ = riderOffsetZ;
        this.defaultRiderPitch = defaultRiderPitch;
        this.ignoredBones = ignoredBones;
        this.riderBone = riderBone;
        addLayer(new DragonRiderLayer<>(this, this.riderOffsetX, this.riderOffsetY, this.riderOffsetZ, this.riderBone, this.defaultRiderPitch, this.ignoredBones));
        if (separatedSaddleLayer) addLayer(new DragonSaddleLayer<>(this));
    }

    @Override
    public void render(T animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource,
                       int packedLight) {
        updateSaddle(animatable);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public abstract void updateSaddle(T entity);
}
