package nordmods.uselessreptile.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.LightningChaser;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningChaserRenderer extends URRideableDragonEntityRenderer<LightningChaser> {
    public static final Map<UUID, Vector3f> headPos = new HashMap<>();

    public LightningChaserRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    @Override
    public void afterSubmit(LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        super.afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        Vector4f vec = getModel(state).getLocatorTransformation("breath").matrix().transform(new Vector4f(0.0F, 0.0F, 0.0F, 1.0F));
        Vector3f pos = new Vector3f(vec.x, vec.y, vec.z).rotate(Axis.YN.rotationDegrees(state.bodyRot + 180)).add((float) state.x, (float) state.y, (float) state.z);
        headPos.put(state.getStateData(URStateDataTypes.DRAGON_UUID), new Vector3f(pos.x, pos.y, pos.z));
    }

    @Override
    public void extractRenderState(LightningChaser animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        renderState.setStateData(URStateDataTypes.DRAGON_UUID, animatable.getUUID());
    }
}
