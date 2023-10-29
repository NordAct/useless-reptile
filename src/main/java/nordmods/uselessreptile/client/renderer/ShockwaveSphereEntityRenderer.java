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
    private float prevA = 1f;

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
            float a = MathHelper.clamp(1f - entity.getCurrentRadius()/entity.maxRadius, 0f, 1f);
            a = MathHelper.lerp(tickDelta, prevA, a);
            for (int j = 0; j < ShockwaveSphereEntity.SPHERE_ROWS; j++) {
                for (int i = 0; i < entity.sphereDots.size()/ShockwaveSphereEntity.SPHERE_ROWS - 1; i++) {
                    //outside
                    Vector3f v0 = getRelativePos(entity.sphereDots.get(1 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    Vector3f v1 = getRelativePos(entity.sphereDots.get(0 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    Vector3f v2 = getRelativePos(entity.sphereDots.get(0 + i * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    Vector3f v3 = getRelativePos(entity.sphereDots.get(1 + i * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    renderQuad(matrixStack.peek().getPositionMatrix(), matrixStack.peek().getNormalMatrix(), vertexConsumer, 1f, 1f, 1f, a, v0, v1, v2, v3);
                    //inside
                    //Vector3f v4 = getRelativePos(entity.sphereDots.get(1 + i * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    //Vector3f v5 = getRelativePos(entity.sphereDots.get(0 + i * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    //Vector3f v6 = getRelativePos(entity.sphereDots.get(0 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    //Vector3f v7 = getRelativePos(entity.sphereDots.get(1 + (i + 1) * ShockwaveSphereEntity.SPHERE_ROWS + j), entity.getPos());
                    //renderQuad(matrixStack.peek().getPositionMatrix(), matrixStack.peek().getNormalMatrix(), vertexConsumer, v4, v5, v6, v7);
                }
            }
            prevA = a;
        }
        matrixStack.pop();
    }

    private static void renderQuad(
            Matrix4f positionMatrix,
            Matrix3f normalMatrix,
            VertexConsumer vertices,
            Vector3f v0,
            Vector3f v1,
            Vector3f v2,
            Vector3f v3
    ) {
        renderQuad(positionMatrix, normalMatrix, vertices, 1, 1, 1, 1, v0, v1, v2, v3);
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
        vertices.vertex(positionMatrix, v1.x, v1.y, v1.z) //01
                .color(r, g, b, a).texture(0, 1)
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
        vertices.vertex(positionMatrix, v3.x, v3.y, v3.z) //10
                .color(r, g, b, a).texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .next();
    }

    private static Vector3f getRelativePos (Vec3d vec3d, Vec3d pos) {
        return new Vector3f((float) (vec3d.x - pos.x), (float) (vec3d.y - pos.y), (float) (vec3d.z - pos.z));
    }
}
