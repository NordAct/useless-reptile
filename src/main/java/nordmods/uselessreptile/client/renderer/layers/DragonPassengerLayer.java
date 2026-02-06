package nordmods.uselessreptile.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import libs.gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.uselessreptile.client.init.URStateDataTypes;

import java.util.*;

public class DragonPassengerLayer extends nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer {
    public static final Set<UUID> PASSENGERS = new HashSet<>();

    public DragonPassengerLayer(BRRenderer<? extends BRState> renderer) {
        super(renderer);
    }

    @Override
    protected void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        List<Boolean> shouldRenderToClient = state.getStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT);
        List<? super EntityRenderState> renderStates = state.getStateData(URStateDataTypes.PASSENGERS_RENDER_STATE);
        List<EntityRenderer<? super Entity, ? super EntityRenderState>> renderers = state.getStateData(URStateDataTypes.PASSENGERS_RENDERERS);
        List<UUID> uuids = state.getStateData(URStateDataTypes.PASSENGERS_UUID);
        List<Vec3> attachmentPos = state.getStateData(URStateDataTypes.PASSENGERS_ATTACHMENT_POS);

        for (int i = 0; i < shouldRenderToClient.size(); i++) {
            if (!shouldRenderToClient.get(i)) continue;

            LocatorTransformation transformation = getModel(state).getLocatorTransformation("passenger" + i);
            if (transformation == null) continue;

            UUID passengerUUID = uuids.get(i);

            PASSENGERS.remove(passengerUUID);
            poseStack.pushPose();

            Vec3 vec3d = attachmentPos.get(i);
            float scale = 1/(state.getStateData(StateDataTypes.SCALE, 1f));
            poseStack.scale(-1, -1, 1);
            poseStack.mulPose(transformation.matrix());
            poseStack.scale(-1, -1, 1);
            poseStack.translate(-vec3d.x * scale, -vec3d.y * scale, -vec3d.z * scale);
            poseStack.scale(scale, scale, scale);
            EntityRenderState passengerState = (EntityRenderState) renderStates.get(i);
            if (passengerState instanceof EntityRenderState renderState) renderState.nameTag = null;
            renderers.get(i).submit(
                    passengerState,
                    poseStack,
                    submitNodeCollector,
                    cameraRenderState
            );

            poseStack.popPose();
            PASSENGERS.add(passengerUUID);
        }
    }

    @Override
    public boolean canRender(BRState state) {
        return !state.getStateData(URStateDataTypes.PASSENGERS_RENDER_STATE).isEmpty();
    }
}