package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShockwaveSphereEntityRenderer extends EntityRenderer<ShockwaveSphereEntity> {
    private float prevAlpha = 1f;
    private static final int SPHERE_ROWS = 16;

    public ShockwaveSphereEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(ShockwaveSphereEntity entity) {
        return new Identifier(UselessReptile.MODID, "textures/misc/shockwave.png");
    }

    @Override
    public void render(ShockwaveSphereEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();

        float alpha = MathHelper.clamp(1f - entity.getCurrentRadius() / entity.maxRadius, 0f, 1f);
        alpha = MathHelper.lerp(tickDelta, prevAlpha, alpha);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity), true));
        matrixStack.translate(0, entity.getCurrentRadius(), 0);
        renderSphere(entity, matrixStack, vertexConsumer, alpha);

        prevAlpha = alpha;
        matrixStack.pop();
    }

    private void renderQuad(
            Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices,
            Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
            float a,
            float minU, float maxU, float minV, float maxV
    ) {
        vertices.vertex(positionMatrix, v0.x, v0.y, v0.z) //00
                .color(1, 1, 1, a).texture(minU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v1.x, v1.y, v1.z) //10
                .color(1, 1, 1, a).texture(maxU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v2.x, v2.y, v2.z) //11
                .color(1, 1, 1, a).texture(maxU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v3.x, v3.y, v3.z) //01
                .color(1, 1, 1, a).texture(minU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
    }

    private void renderSphere(ShockwaveSphereEntity entity, MatrixStack matrixStack, VertexConsumer vertexConsumer, float alpha) {
        float dPhi = (float) (-Math.PI / SPHERE_ROWS);
        float dTheta = (float) (-2 * Math.PI / SPHERE_ROWS);

        for (int i = 0; i < SPHERE_ROWS; i++) {
            float minV = i / (float) SPHERE_ROWS;
            float maxV = (i + 1f) / (float) SPHERE_ROWS;

            float minPhi = i * dPhi;
            float maxPhi = (i + 1) * dPhi;

            for (int j = 0; j < SPHERE_ROWS; j++) {
                float minU =  j / (float) SPHERE_ROWS;
                float maxU = (j + 1) / (float) SPHERE_ROWS;

                float minTheta = j * dTheta;
                float maxTheta = (j + 1) * dTheta;

                float radius = entity.getCurrentRadius();

                Vector3f v0 = getSphereDot(minPhi, minTheta, radius);
                Vector3f v1 = getSphereDot(minPhi, maxTheta, radius);
                Vector3f v2 = getSphereDot(maxPhi, maxTheta, radius);
                Vector3f v3 = getSphereDot(maxPhi, minTheta, radius);

                renderQuad(matrixStack.peek().getPositionMatrix(), matrixStack.peek().getNormalMatrix(), vertexConsumer,
                        v0, v1, v2, v3, alpha, minU, maxU, minV, maxV);
            }
        }
    }

    private Vector3f getSphereDot(float phi, float theta, float radius) {
        float x = (float) (Math.sin(phi) * Math.cos(theta));
        float y = (float) Math.cos(phi);
        float z = (float) (Math.sin(phi) * Math.sin(theta));
        return new Vector3f(x, y, z).mul(radius);
    }
}
