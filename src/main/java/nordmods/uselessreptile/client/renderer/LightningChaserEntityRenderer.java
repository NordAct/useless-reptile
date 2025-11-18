package nordmods.uselessreptile.client.renderer;

import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;

public class LightningChaserEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<LightningChaserEntity, R> {
    public static final Map<UUID, Vector3f> headPos = new HashMap<>();

    public LightningChaserEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    public void renderBone(R renderState, PoseStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState,
                           int packedLight, int packedOverlay, int renderColor) {
        super.renderBone(renderState, poseStack, bone, buffer, cameraState, packedLight, packedOverlay, renderColor);
        if (bone.getName().equals("head")) {
            Vector3d vector3d = bone.getLocalPosition();
            headPos.put(renderState.getGeckolibData(URDataTickets.DRAGON_UUID), new Vector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z));
        }
    }

    @Override
    public void addRenderData(LightningChaserEntity animatable, Void relatedObject, R renderState, float tickDelta) {
        super.addRenderData(animatable, relatedObject, renderState, tickDelta);
        renderState.addGeckolibData(URDataTickets.DRAGON_UUID, animatable.getUUID());
    }
}
