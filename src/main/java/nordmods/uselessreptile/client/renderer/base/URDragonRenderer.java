package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public abstract class URDragonRenderer <T extends URDragonEntity> extends GeoEntityRenderer<T> {
    public URDragonRenderer(EntityRendererFactory.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        addRenderLayer(new URGlowingLayer<>(this));
    }

    @Override
    public void preRender(MatrixStack poseStack, T animatable, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        scaleWidth = scaleHeight = animatable.getScale();
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    protected float getShadowRadius(T entity) {
        return super.shadowRadius * entity.getScale();
    }

    @Override
    public void postRender(MatrixStack poseStack, T dragon, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        DragonAssetCache dragonAssetCache = dragon.getAssetCache();

        int i = 0;
        for (ItemStack itemStack : dragon.getArmorItems()) {
            int j = i;
            i++;
            if (itemStack.isEmpty() || !ResourceUtil.isResourceReloadFinished) {
                dragonAssetCache.setEquipmentAnimatable(j, null);
                continue;
            }

            DragonEquipmentRenderer dragonEquipmentRenderer = new DragonEquipmentRenderer();
            DragonEquipmentAnimatable dragonEquipmentAnimatable = dragonAssetCache.getEquipmentAnimatable(j);
            if (dragonEquipmentAnimatable == null || dragonEquipmentAnimatable.item != itemStack.getItem()) {
                dragonEquipmentAnimatable = new DragonEquipmentAnimatable(dragon, itemStack.getItem());
                dragonAssetCache.setEquipmentAnimatable(j, dragonEquipmentAnimatable);
            }

            Identifier id = dragonEquipmentRenderer.getGeoModel().getModelResource(dragonEquipmentAnimatable);
            if (id == null) continue;
            BakedGeoModel bakedEquipmentModel = dragonEquipmentRenderer.getGeoModel().getBakedModel(id);
            id = dragonEquipmentRenderer.getGeoModel().getTextureResource(dragonEquipmentAnimatable);
            if (id == null) continue;

            Map<String, GeoBone> equipmentBones = dragonEquipmentAnimatable.equipmentBones;
            if (equipmentBones.isEmpty()) getSaddleBones(equipmentBones, bakedEquipmentModel);

            getGeoModel().getAnimationProcessor().getRegisteredBones().forEach(bone -> {
                GeoBone equipmentBone = equipmentBones.get(bone.getName());
                if (equipmentBone != null) {
                    equipmentBone.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    equipmentBone.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    equipmentBone.updatePosition(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            });

            RenderLayer cameo = dragonEquipmentRenderer.getGeoModel().getRenderType(dragonEquipmentAnimatable, id);
            dragonEquipmentRenderer.render(poseStack, dragonEquipmentAnimatable, bufferSource, cameo, bufferSource.getBuffer(cameo), packedLight);
        }
    }

    private static void addChildren(Map<String, GeoBone> equipmentBones, GeoBone bone) {
        equipmentBones.put(bone.getName(), bone);
        for (GeoBone child : bone.getChildBones()) addChildren(equipmentBones, child);
    }

    private static void getSaddleBones(Map<String, GeoBone> equipmentBones, BakedGeoModel model) {
        //equipmentBones.clear();
        for (GeoBone bone : model.topLevelBones()) addChildren(equipmentBones, bone);
    }

}
