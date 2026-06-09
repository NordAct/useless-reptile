package nordmods.uselessreptile.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {
    public static void renderQuad(
            Matrix4f positionMatrix, PoseStack.Pose normalMatrix, VertexConsumer vertices,
            Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
            float a, float r, float g, float b, int light,
            float minU, float maxU, float minV, float maxV
    ) {
        vertices.addVertex(positionMatrix, v0.x, v0.y, v0.z) //00
                .setColor(r, g, b, a).setUv(minU, minV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.addVertex(positionMatrix, v1.x, v1.y, v1.z) //10
                .setColor(r, g, b, a).setUv(maxU, minV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.addVertex(positionMatrix, v2.x, v2.y, v2.z) //11
                .setColor(r, g, b, a).setUv(maxU, maxV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.addVertex(positionMatrix, v3.x, v3.y, v3.z) //01
                .setColor(r, g, b, a).setUv(minU, maxV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normalMatrix, 0.0F, 1.0F, 0.0F);
    }

    public static <E extends Entity> EntityRenderer<? super E, ? super EntityRenderState> getEntityRenderer(E entityIn) {
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        return (EntityRenderer<? super E, ? super EntityRenderState>) manager.getRenderer(entityIn);
    }

    public static float getTickDelta(boolean ignoreFreeze) {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(ignoreFreeze);
    }

    public static CameraRenderState getCameraRenderState() {
        return Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
    }
}
