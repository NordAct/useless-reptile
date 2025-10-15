package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.init.URTags;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WyvernEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<WyvernEntity, R> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    @Override
    public void preRender(R renderState, MatrixStack poseStack, BakedGeoModel model, OrderedRenderCommandQueue renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        updateSaddle(renderState);
        super.preRender(renderState, poseStack, model, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
    }

    protected void updateSaddle (R renderState) {
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(renderState.getGeckolibData(URDataTickets.DRAGON_HAS_SADDLE)));
    }

    @Override
    public void addRenderData(WyvernEntity animatable, Void relatedObject, R renderState, float tickDelta) {
        super.addRenderData(animatable, relatedObject, renderState, tickDelta);
        renderState.addGeckolibData(URDataTickets.DRAGON_HAS_SADDLE, animatable.getEquippedStack(EquipmentSlot.SADDLE).isIn(URTags.WYVERN_SADDLES));
    }
}
