package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LightningChaserEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<LightningChaserEntity, R> {
    public static final Map<UUID, Vector3f> headPos = new HashMap<>();

    public LightningChaserEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }

    public void renderBone(R renderState, MatrixStack poseStack, GeoBone bone, VertexConsumer buffer, CameraRenderState cameraState, boolean skipBoneTasks,
                           int packedLight, int packedOverlay, int renderColor) {
        super.renderBone(renderState, poseStack, bone, buffer, cameraState, skipBoneTasks, packedLight, packedOverlay, renderColor);
        if (bone.getName().equals("head")) {
            Vector3d vector3d = bone.getLocalPosition();
            headPos.put(renderState.getGeckolibData(URDataTickets.DRAGON_UUID), new Vector3f((float) vector3d.x, (float) vector3d.y, (float) vector3d.z));
        }
    }

    @Override
    public void addRenderData(LightningChaserEntity animatable, Void relatedObject, R renderState, float tickDelta) {
        super.addRenderData(animatable, relatedObject, renderState, tickDelta);
        renderState.addGeckolibData(URDataTickets.DRAGON_UUID, animatable.getUuid());
    }
}
