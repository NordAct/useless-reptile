package nordmods.uselessreptile.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderUtil {
    public static void renderQuad(
            Matrix4f positionMatrix, MatrixStack.Entry normalMatrix, VertexConsumer vertices,
            Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
            float a, float r, float g, float b, int light,
            float minU, float maxU, float minV, float maxV
    ) {
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

    public static <E extends Entity, S extends EntityRenderState> void renderEntity(E entityIn, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
        try {
            EntityRenderer<? super E, S> render = (EntityRenderer<? super E, S>) getEntityRenderer(entityIn); //this is cursed and may crash if someone decides they want to be a special bean and have something else as state
            render.render(render.getAndUpdateRenderState(entityIn, tickDelta), matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new CrashException(CrashReport.create(throwable1, "Rendering entity in world"));
        }
    }

    public static <E extends Entity> EntityRenderer<? super E, ?> getEntityRenderer(E entityIn) {
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        return manager.getRenderer(entityIn);
    }

    public static float getTickDelta(boolean ignoreFreeze) {
        return MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(ignoreFreeze);
    }
}
