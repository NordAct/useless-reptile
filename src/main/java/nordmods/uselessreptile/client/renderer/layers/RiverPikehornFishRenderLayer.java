package nordmods.uselessreptile.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.renderer.layer.ItemRenderLayer;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import org.jetbrains.annotations.Nullable;

public class RiverPikehornFishRenderLayer extends ItemRenderLayer {
    public RiverPikehornFishRenderLayer(BRRenderer<?> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    protected void beforeSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        super.beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        ItemStackRenderState stackRenderState = this.getItemStackRenderState(state);
        if (stackRenderState != null) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45f));
        }
    }

    @Override
    protected String getLocatorName() {
        return "fish";
    }

    @Override
    protected @Nullable ItemStackRenderState getItemStackRenderState(BRState state) {
        return state.getStateData(URStateDataTypes.MAIN_HAND);
    }
}
