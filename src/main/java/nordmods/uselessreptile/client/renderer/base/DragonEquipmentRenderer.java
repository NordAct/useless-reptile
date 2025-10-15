package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.model.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable, GeoModel<?>, GeoRenderState> { //related object - dragon model and render state
    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        withRenderLayer(new URGlowingLayer<>(this, state -> state.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE)));
    }

    @Override
    public void preRender(GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        renderState.getGeckolibData(URDataTickets.DRAGON_BONES).forEach((parentBone, transform) -> {
            model.getBone((String) parentBone).ifPresent(bone -> {
                Vector3f rot = ((OwnerBoneTransforms)transform).rot;
                bone.setRotX(rot.x);
                bone.setRotY(rot.y);
                bone.setRotZ(rot.z);

                Vector3f pos = ((OwnerBoneTransforms)transform).pos;
                bone.setPosX(pos.x);
                bone.setPosY(pos.y);
                bone.setPosZ(pos.z);

                Vector3f scale = ((OwnerBoneTransforms)transform).scale;
                bone.setScaleX(scale.x);
                bone.setScaleY(scale.y);
                bone.setScaleZ(scale.z);
            });
        });
        super.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
    }

    @Override
    public void adjustRenderPose(GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, CameraRenderState cameraState) {
    }

    @Override
    public void addRenderData(DragonEquipmentAnimatable animatable, GeoModel<?> relatedObject, GeoRenderState renderState, float partialTick) {
        renderState.addGeckolibData(URDataTickets.DRAGON_RENDER_STATE, animatable.ownerRenderState);
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID, Registries.ITEM.getId(animatable.item));
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE, animatable.getAssetCache());

        Identifier id = getGeoModel().getModelResource(renderState);
        HashMap<String, OwnerBoneTransforms> transforms = new HashMap<>();
        if (id != DragonEqupmentModel.DEFAULT_MODEL) {
            Map<String, GeoBone> equipmentBones = animatable.equipmentBones;
            if (equipmentBones.isEmpty()) {
                BakedGeoModel bakedEquipmentModel = getGeoModel().getBakedModel(id);
                getEquipmentBones(equipmentBones, bakedEquipmentModel);
            }

            relatedObject.getAnimationProcessor().getRegisteredBones().forEach(bone -> {
                GeoBone equipmentBone = animatable.equipmentBones.get(bone.getName());
                if (equipmentBone != null) {
                    transforms.put(bone.getName(),
                            new OwnerBoneTransforms(
                                    new Vector3f(bone.getPosX(), bone.getPosY(), bone.getPosZ()),
                                    new Vector3f(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()),
                                    new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ())
                                    )
                    );
                }
            });
        }
        renderState.addGeckolibData(URDataTickets.DRAGON_BONES, transforms);
    }

    public record OwnerBoneTransforms(Vector3f pos, Vector3f scale, Vector3f rot) {
    }


    private void addChildren(Map<String, GeoBone> equipmentBones, GeoBone bone) {
        equipmentBones.put(bone.getName(), bone);
        for (GeoBone child : bone.getChildBones()) addChildren(equipmentBones, child);
    }

    private void getEquipmentBones(Map<String, GeoBone> equipmentBones, BakedGeoModel model) {
        for (GeoBone bone : model.topLevelBones()) addChildren(equipmentBones, bone);
    }
}