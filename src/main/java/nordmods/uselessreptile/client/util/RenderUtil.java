package nordmods.uselessreptile.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {
    public static void renderQuad( //todo test if it works
            Matrix4f positionMatrix, MatrixStack.Entry normalMatrix, RenderLayer layer,
            Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
            float a, float r, float g, float b, int light,
            float minU, float maxU, float minV, float maxV
    ) {
        VertexConsumer vertices = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(layer);

        vertices.vertex(positionMatrix, v0.x, v0.y, v0.z) //00
                .color(r, g, b, a).texture(minU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.vertex(positionMatrix, v1.x, v1.y, v1.z) //10
                .color(r, g, b, a).texture(maxU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.vertex(positionMatrix, v2.x, v2.y, v2.z) //11
                .color(r, g, b, a).texture(maxU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F);
        vertices.vertex(positionMatrix, v3.x, v3.y, v3.z) //01
                .color(r, g, b, a).texture(minU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F);
    }

    public static <E extends Entity> EntityRenderer<? super E, ?> getEntityRenderer(E entityIn) {
        EntityRenderManager manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        return manager.getRenderer(entityIn);
    }

    public static float getTickDelta(boolean ignoreFreeze) {
        return MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(ignoreFreeze);
    }

    public static CameraRenderState getCameraRenderState() {
        return MinecraftClient.getInstance().gameRenderer.getEntityRenderStates().cameraRenderState;
    }
}
