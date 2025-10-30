package nordmods.uselessreptile.client.renderer.base;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.model.URDragonModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.renderer.special.SaddleEquipmentRenderer;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.ShooterDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URTags;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public abstract class URDragonEntityRenderer<T extends URDragonEntity, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {
    private final DragonEquipmentRenderer dragonEquipmentRenderer = new DragonEquipmentRenderer();
    private final SaddleEquipmentRenderer saddleEquipmentRenderer = new SaddleEquipmentRenderer();
    public URDragonEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new URDragonModel<>());
        withRenderLayer(new URGlowingLayer<>(this, state -> state.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE)));
    }

    @Override
    protected float getShadowRadius(R state) {
        return super.getShadowRadius(state) * state.baseScale;
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState,
                           int packedLight, int packedOverlay, int renderColor) {
        super.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
        if (renderState.hitbox != null && renderState.hasGeckolibData(URDataTickets.DRAGON_SHOOTING_POINT) //mayhaps I should move this to mixin
                && renderTasks instanceof OrderedRenderCommandQueue orderedRenderCommandQueue
                && orderedRenderCommandQueue.getBatchingQueue(0) instanceof BatchingRenderCommandQueue queue) {
            queue.useless_reptile$submitShootingPoint(poseStack, renderState, renderState.getGeckolibData(URDataTickets.DRAGON_SHOOTING_POINT));
        }
    }

    @Override
    public void postRender(R dragonRenderState, MatrixStack poseStack, BakedGeoModel model, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState,
                           int packedLight, int packedOverlay, int renderColor) {
        super.postRender(dragonRenderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
        if (!ResourceUtil.isResourceReloadFinished) return;

        DragonAssetCache dragonAssetCache = dragonRenderState.getGeckolibData(URDataTickets.DRAGON_ASSET_CACHE);
        EntityEquipment equipment = dragonRenderState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT);

        float tickDelta = dragonRenderState.getGeckolibData(DataTickets.PARTIAL_TICK);
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
            }

            DragonEquipmentRenderer usedRenderer = itemStack.isIn(URTags.DRAGON_SADDLES) ? saddleEquipmentRenderer : dragonEquipmentRenderer;
            usedRenderer.submit(poseStack, dragonEquipmentAnimatable, getGeoModel(), renderTasks, cameraState, packedLight, tickDelta, null);
        }
    }


    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(URDataTickets.DRAGON_ID, animatable.getDragonId());
        renderState.addGeckolibData(URDataTickets.DRAGON_VARIANT, animatable.getVariant());
        renderState.addGeckolibData(URDataTickets.DRAGON_NAME, animatable.getCustomName());
        renderState.addGeckolibData(URDataTickets.DRAGON_ASSET_CACHE, animatable.getAssetCache());
        if (animatable instanceof ShooterDragon shooterDragon) renderState.addGeckolibData(URDataTickets.DRAGON_SHOOTING_POINT, shooterDragon.getShootingPoint());

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