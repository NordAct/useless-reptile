package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.LightningChaser;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningChaserRenderer extends URRideableDragonEntityRenderer<LightningChaser> {
    public static final Map<UUID, Vector3f> headPos = new HashMap<>();

    public LightningChaserRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }
    //todo
//    public void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState,
//                           int packedLight, int packedOverlay, int renderColor) {
//        super.renderBone(renderState, poseStack, bone, buffer, cameraState, packedLight, packedOverlay, renderColor);
//        if (bone.getName().equals("head")) {
//            Vector3d vector3d = bone.getLocalPosition();
//            headPos.put(renderState.getGeckolibData(URStateDataTypes.DRAGON_UUID), new Vector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z));
//        }
//    }
//
//    @Override
//    public void addRenderData(LightningChaser animatable, Void relatedObject, R renderState, float tickDelta) {
//        super.addRenderData(animatable, relatedObject, renderState, tickDelta);
//        renderState.addGeckolibData(URStateDataTypes.DRAGON_UUID, animatable.getUUID());
//    }
}
