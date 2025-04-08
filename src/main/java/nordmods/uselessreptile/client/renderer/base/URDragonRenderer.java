package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.model.URDragonModel;
import nordmods.uselessreptile.client.model.special.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.renderer.special.SaddleEquipmentRenderer;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public abstract class URDragonRenderer <T extends URDragonEntity> extends GeoEntityRenderer<T> {
    private final DragonEquipmentRenderer dragonEquipmentRenderer = new DragonEquipmentRenderer();
    private final SaddleEquipmentRenderer saddleEquipmentRenderer = new SaddleEquipmentRenderer();
    public URDragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new URDragonModel<>());
        addRenderLayer(new URGlowingLayer<>(this));
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return super.getShadowRadius(state) * ((LivingEntityRenderState)state).baseScale;
    }

    @Override
    public void postRender(MatrixStack poseStack, T dragon, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.postRender(poseStack, dragon, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (!ResourceUtil.isResourceReloadFinished) return;

        DragonAssetCache dragonAssetCache = dragon.getAssetCache();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = dragon.getEquippedStack(slot);
            if (itemStack.isEmpty()) {
                dragonAssetCache.setEquipmentAnimatable(slot, null);
                continue;
            }

            DragonEquipmentAnimatable dragonEquipmentAnimatable = dragonAssetCache.getEquipmentAnimatable(slot);
            if (dragonEquipmentAnimatable == null || dragonEquipmentAnimatable.item != itemStack.getItem()) {
                dragonEquipmentAnimatable = new DragonEquipmentAnimatable(dragon, itemStack.getItem());
                dragonAssetCache.setEquipmentAnimatable(slot, dragonEquipmentAnimatable);
            }

            DragonEquipmentRenderer usedRenderer = itemStack.isIn(URTags.DRAGON_SADDLES) ? saddleEquipmentRenderer : dragonEquipmentRenderer;

            Identifier id = usedRenderer.getGeoModel().getModelResource(dragonEquipmentAnimatable, usedRenderer);
            if (id == DragonEqupmentModel.DEFAULT_MODEL) continue;
            BakedGeoModel bakedEquipmentModel = usedRenderer.getGeoModel().getBakedModel(id);

            id = usedRenderer.getGeoModel().getTextureResource(dragonEquipmentAnimatable, usedRenderer);
            Map<String, GeoBone> equipmentBones = dragonEquipmentAnimatable.equipmentBones;
            if (equipmentBones.isEmpty()) getEquipmentBones(equipmentBones, bakedEquipmentModel);

            getGeoModel().getAnimationProcessor().getRegisteredBones().forEach(bone -> {
                GeoBone equipmentBone = equipmentBones.get(bone.getName());
                if (equipmentBone != null) {
                    equipmentBone.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    equipmentBone.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    equipmentBone.updatePosition(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            });

            RenderLayer renderType = usedRenderer.getGeoModel().getRenderType(dragonEquipmentAnimatable, id);
            usedRenderer.render(poseStack, dragonEquipmentAnimatable, bufferSource, renderType, bufferSource.getBuffer(renderType), packedLight, partialTick);
        }
    }

    private void addChildren(Map<String, GeoBone> equipmentBones, GeoBone bone) {
        equipmentBones.put(bone.getName(), bone);
        for (GeoBone child : bone.getChildBones()) addChildren(equipmentBones, child);
    }

    private void getEquipmentBones(Map<String, GeoBone> equipmentBones, BakedGeoModel model) {
        for (GeoBone bone : model.topLevelBones()) addChildren(equipmentBones, bone);
    }

}