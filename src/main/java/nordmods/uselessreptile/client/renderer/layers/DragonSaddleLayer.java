package nordmods.uselessreptile.client.renderer.layers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.model.special.DragonSaddleModel;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;

public class DragonSaddleLayer <T extends URRideableDragonEntity> extends GeoRenderLayer<T> {
    private final Map<String, CoreGeoBone> saddleBones = new Object2ObjectOpenHashMap<>();
    private DragonSaddleModel<T> saddleModel;
    private BakedGeoModel bakedSaddleModel;

    public DragonSaddleLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void preRender(MatrixStack poseStack, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                          VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                          int packedLight, int packedOverlay) {
        saddleModel = new DragonSaddleModel<>(entity);
        bakedSaddleModel = saddleModel.getBakedModel(saddleModel.getModelResource(entity));
        getSaddleBones(bakedSaddleModel);
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (bakedSaddleModel != null) {
            getGeoModel().getAnimationProcessor().getRegisteredBones().forEach(bone -> {
                GeoBone saddleBone = (GeoBone) saddleBones.get(bone.getName());
                if (saddleBone != null) {
                    saddleBone.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    saddleBone.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    saddleBone.updatePosition(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            });
            RenderLayer cameo = RenderLayer.getEntityCutout(saddleModel.getTextureResource(entity));
            getRenderer().reRender(bakedSaddleModel, matrixStackIn, bufferSource, entity, cameo,
                    bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                    1, 1, 1, 1);
        }
    }

    @Override
    public void renderForBone(MatrixStack matrixStackIn, T entity, GeoBone bone, RenderLayer renderType,
                              VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

    }

    private void addChildren(CoreGeoBone bone) {
        saddleBones.put(bone.getName(), bone);
        for (CoreGeoBone child : bone.getChildBones()) addChildren(child);
    }

    private void getSaddleBones(CoreBakedGeoModel model) {
        saddleBones.clear();
        for (CoreGeoBone bone : model.getBones()) addChildren(bone);
    }
}
