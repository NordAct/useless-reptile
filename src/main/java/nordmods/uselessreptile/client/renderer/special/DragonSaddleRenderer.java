package nordmods.uselessreptile.client.renderer.special;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.model.special.DragonSaddleModel;
import nordmods.uselessreptile.client.renderer.base.URDragonRenderer;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonRenderer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.special.RenderOnlyEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public class DragonSaddleRenderer <E extends URRideableDragonEntity, T extends RenderOnlyEntity<E>> extends GeoEntityRenderer<T> {
    private final Map<String, CoreGeoBone> saddleBones = new Object2ObjectOpenHashMap<>();
    public DragonSaddleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DragonSaddleModel<>());
    }

    @Override
    public long getInstanceId(T animatable) {
        return animatable.owner.getId();
    }

    @Override
    public void preRender(MatrixStack poseStack, T entity, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        getSaddleBones(model);
        URRideableDragonRenderer<E> ownerRenderer = getOwnerRenderer(entity);
        ownerRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones().forEach(bone -> {
            GeoBone saddleBone = (GeoBone) saddleBones.get(bone.getName());
            if (saddleBone != null) {
                saddleBone.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                saddleBone.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                saddleBone.updatePosition(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            }
        });
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void addChildren(CoreGeoBone bone) {
        saddleBones.put(bone.getName(), bone);
        for (CoreGeoBone child : bone.getChildBones()) addChildren(child);
    }

    private void getSaddleBones(CoreBakedGeoModel model) {
        saddleBones.clear();
        for (CoreGeoBone bone : model.getBones()) addChildren(bone);
    }

    private URRideableDragonRenderer<E> getOwnerRenderer(T entity) {
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        return (URRideableDragonRenderer<E>) manager.getRenderer(entity.owner);
    }
}
