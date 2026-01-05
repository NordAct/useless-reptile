package nordmods.uselessreptile.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import libs.gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.Wyvern;
import nordmods.uselessreptile.common.init.URTags;

public class WyvernRenderer extends URRideableDragonEntityRenderer<Wyvern> {
    public WyvernRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    @Override
    public void beforeSubmit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        AnimatedBone bone = getModel(state).getBone("spikes_front");
        if (bone != null) bone.setVisible(!state.getStateData(URStateDataTypes.DRAGON_HAS_SADDLE, false));
        super.beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public void extractRenderState(Wyvern animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        renderState.setStateData(URStateDataTypes.DRAGON_HAS_SADDLE, animatable.getItemBySlot(EquipmentSlot.SADDLE).is(URTags.WYVERN_SADDLES));
    }
}
