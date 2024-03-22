package nordmods.uselessreptile.client.renderer.layers.equipment;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.model.special.DragonEqupmentModel;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;

public abstract class URDragonEquipmentLayer<T extends URDragonEntity> extends GeoRenderLayer<T> {
    private final Map<String, CoreGeoBone> saddleBones = new Object2ObjectOpenHashMap<>();
    private DragonEqupmentModel<T> equpmentModel;
    private BakedGeoModel bakedEquipmentModel;
    private final EquipmentSlot equipmentSlot;

    public URDragonEquipmentLayer(GeoRenderer<T> entityRendererIn, EquipmentSlot equipmentSlot) {
        super(entityRendererIn);
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public void preRender(MatrixStack poseStack, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                          VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                          int packedLight, int packedOverlay) {
        ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
        if (itemStack.isEmpty()) {
            equpmentModel = null;
            bakedEquipmentModel = null;
            return;
        }

        equpmentModel = new DragonEqupmentModel<>(entity, itemStack.getItem());
        Identifier id = equpmentModel.getModelResource(entity);
        if (!ResourceUtil.doesExist(id)) return;

        bakedEquipmentModel = equpmentModel.getBakedModel(id);
        getSaddleBones(bakedEquipmentModel);
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (bakedEquipmentModel == null) return;
        Identifier id = equpmentModel.getTextureResource(entity);
        if (!ResourceUtil.doesExist(id)) return;

        getGeoModel().getAnimationProcessor().getRegisteredBones().forEach(bone -> {
            GeoBone saddleBone = (GeoBone) saddleBones.get(bone.getName());
            if (saddleBone != null) {
                saddleBone.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                saddleBone.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                saddleBone.updatePosition(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            }
        });

        RenderLayer cameo = equpmentModel.getRenderType(entity, id);
        getRenderer().reRender(bakedEquipmentModel, matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
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
