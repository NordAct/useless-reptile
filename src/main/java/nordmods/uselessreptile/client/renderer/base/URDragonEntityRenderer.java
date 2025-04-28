package nordmods.uselessreptile.client.renderer.base;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.model.DragonEqupmentModel;
import nordmods.uselessreptile.client.model.URDragonModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.renderer.special.SaddleEquipmentRenderer;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URTags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

public abstract class URDragonEntityRenderer<T extends URDragonEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    private final DragonEquipmentRenderer dragonEquipmentRenderer = new DragonEquipmentRenderer();
    private final SaddleEquipmentRenderer saddleEquipmentRenderer = new SaddleEquipmentRenderer();
    public URDragonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new URDragonModel<>());
        addRenderLayer(new URGlowingLayer<>(this, state -> state.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE)));
    }

    @Override
    protected float getShadowRadius(R state) {
        return super.getShadowRadius(state) * state.baseScale;
    }

    @Override
    public void postRender(R dragonRenderState, MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        super.postRender(dragonRenderState, poseStack, model, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
        if (!ResourceUtil.isResourceReloadFinished) return;

        DragonAssetCache dragonAssetCache = dragonRenderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        EntityEquipment equipment = dragonRenderState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = equipment.get(slot);
            if (itemStack == null || itemStack.isEmpty()) {
                dragonAssetCache.setEquipmentAnimatable(slot, null);
                continue;
            }

            DragonEquipmentAnimatable dragonEquipmentAnimatable = dragonAssetCache.getEquipmentAnimatable(slot);
            if (dragonEquipmentAnimatable == null || dragonEquipmentAnimatable.item != itemStack.getItem()) {
                dragonEquipmentAnimatable = new DragonEquipmentAnimatable(dragonRenderState, itemStack.getItem());
                dragonAssetCache.setEquipmentAnimatable(slot, dragonEquipmentAnimatable);
            } else dragonEquipmentAnimatable.ownerRenderState = dragonRenderState;

            DragonEquipmentRenderer usedRenderer = itemStack.isIn(URTags.DRAGON_SADDLES) ? saddleEquipmentRenderer : dragonEquipmentRenderer;
            GeoRenderState temp = new GeoRenderState.Impl();
            usedRenderer.addRenderData(dragonEquipmentAnimatable, null, temp);

            Identifier id = usedRenderer.getGeoModel().getModelResource(temp);
            if (id == DragonEqupmentModel.DEFAULT_MODEL) continue;
            BakedGeoModel bakedEquipmentModel = usedRenderer.getGeoModel().getBakedModel(id);

            id = usedRenderer.getGeoModel().getTextureResource(temp);
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

            RenderLayer renderType = usedRenderer.getGeoModel().getRenderType(temp, id);
            usedRenderer.render(poseStack, dragonEquipmentAnimatable, bufferSource, renderType, bufferSource.getBuffer(renderType), packedLight, RenderUtil.getTickDelta(false));
        }
    }

    private void addChildren(Map<String, GeoBone> equipmentBones, GeoBone bone) {
        equipmentBones.put(bone.getName(), bone);
        for (GeoBone child : bone.getChildBones()) addChildren(equipmentBones, child);
    }

    private void getEquipmentBones(Map<String, GeoBone> equipmentBones, BakedGeoModel model) {
        for (GeoBone bone : model.topLevelBones()) addChildren(equipmentBones, bone);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(URDataTickets.DRAGON_ID, animatable.getDragonId());
        renderState.addGeckolibData(URDataTickets.DRAGON_VARIANT, animatable.getVariant());
        renderState.addGeckolibData(URDataTickets.DRAGON_NAME, animatable.getCustomName());
        renderState.addGeckolibData(URDataTickets.DRAGON_ASSET_CACHE, animatable.getAssetCache());

        EntityEquipment map = new EntityEquipment();
        for (EquipmentSlot slot : EquipmentSlot.values()) map.put(slot, animatable.getEquippedStack(slot));
        renderState.addGeckolibData(URDataTickets.DRAGON_EQIPMENT, map);
    }

    @Override
    protected void appendHitboxes(T entity, ImmutableList.Builder<EntityHitbox> builder, float tickDelta) {
        super.appendHitboxes(entity, builder, tickDelta);
        if (URClientConfig.getConfig().attackBoxesInDebug) {
            double x = -entity.getX();
            double y = -entity.getY();
            double z = -entity.getZ();

            Box box = entity.getAttackBox();
            if (box != null) {
                builder.add(new EntityHitbox(
                        box.minX + x,
                        box.minY + y,
                        box.minZ + z,
                        box.maxX + x,
                        box.maxY + y,
                        box.maxZ + z,
                        1,
                        0,
                        1
                ));
            }

            box = entity.getSecondaryAttackBox();
            if (box != null) {
                builder.add(new EntityHitbox(
                        box.minX + x,
                        box.minY + y,
                        box.minZ + z,
                        box.maxX + x,
                        box.maxY + y,
                        box.maxZ + z,
                        1.0F,
                        0.0f,
                        0.25f
                ));
            }
        }
    }
}