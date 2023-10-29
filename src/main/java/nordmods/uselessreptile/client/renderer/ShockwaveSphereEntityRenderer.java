package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShockwaveSphereEntityRenderer extends EntityRenderer<ShockwaveSphereEntity> {
    private float prevAlpha = 1f;

    public ShockwaveSphereEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    //todo
    @Override
    public Identifier getTexture(ShockwaveSphereEntity entity) {
        return new Identifier("textures/block/purple_stained_glass.png");
    }

    @Override
    public void render(ShockwaveSphereEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
        if (entity.sphereDots.size() > 0) {
            float alpha = MathHelper.clamp(1f - entity.getCurrentRadius()/entity.maxRadius, 0f, 1f);
            alpha = MathHelper.lerp(tickDelta, prevAlpha, alpha);
            for (int j = 0; j < ShockwaveSphereEntity.SPHERE_ROWS; j++) {
                for (int i = 0; i < entity.sphereDots.size()/ShockwaveSphereEntity.SPHERE_ROWS - 1; i++) {
                    Vector3f v0 = getRelativePos(entity.sphereDots.get(1 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j));
                    Vector3f v1 = getRelativePos(entity.sphereDots.get(0 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j));
                    Vector3f v2 = getRelativePos(entity.sphereDots.get(0 + i * ShockwaveSphereEntity.SPHERE_ROWS + j));
                    Vector3f v3 = getRelativePos(entity.sphereDots.get(1 + i * ShockwaveSphereEntity.SPHERE_ROWS + j));
                    renderQuad(matrixStack.peek().getPositionMatrix(), matrixStack.peek().getNormalMatrix(), vertexConsumer, 1f, 1f, 1f, alpha, v0, v1, v2, v3);
                }
            }
            prevAlpha = alpha;
        }
        matrixStack.pop();
    }

    private static void renderQuad(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertices,
            float r,
            float g,
            float b,
            float a,
            Vector3f v0,
            Vector3f v1,
            Vector3f v2,
            Vector3f v3
    ) {
        vertices.vertex(positionMatrix, v0.x, v0.y, v0.z) //00
                .color(r, g, b, a).texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v1.x, v1.y, v1.z) //10
                .color(r, g, b, a).texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v2.x, v2.y, v2.z) //11
                .color(r, g, b, a).texture(1, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
        vertices.vertex(positionMatrix, v3.x, v3.y, v3.z) //01
                .color(r, g, b, a).texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
    }

    private static Vector3f getRelativePos (Vec3d vec3d) {
        return new Vector3f((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
    }
}
