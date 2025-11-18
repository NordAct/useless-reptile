package nordmods.uselessreptile.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.init.URTags;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WyvernEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<WyvernEntity, R> {
    public WyvernEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    @Override
    public void preRender(R renderState, PoseStack poseStack, BakedGeoModel model, SubmitNodeCollector renderTasks, CameraRenderState cameraState,
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
        renderState.addGeckolibData(URDataTickets.DRAGON_HAS_SADDLE, animatable.getItemBySlot(EquipmentSlot.SADDLE).is(URTags.WYVERN_SADDLES));
    }
}
