package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import nordmods.uselessreptile.common.entity.special.ShockwaveSphereEntity;
import org.joml.Vector3f;

public class ShockwaveSphereEntityRenderer extends EntityRenderer<ShockwaveSphereEntity, ShockwaveSpereEntityRenderState> {
    private static final Identifier TEXTURE = UselessReptile.id("textures/entity/lightning_breath/beam.png");
    private static final int SPHERE_ROWS = 16;

    public ShockwaveSphereEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public ShockwaveSpereEntityRenderState createRenderState() {
        return new ShockwaveSpereEntityRenderState();
    }

    @Override
    public void render(ShockwaveSpereEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE, true));

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.alpha / 2f * 180f));
        renderSphere(matrixStack, vertexConsumer, MathHelper.clamp(state.alpha - 0.2f, 0, 1), state.radius);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.alpha / 1.5f * 180f));
        renderSphere(matrixStack, vertexConsumer, MathHelper.clamp(state.alpha/1.5f - 0.1f, 0, 1), state.radius/1.5f);
        matrixStack.pop();
        
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.alpha * 180f));
        renderSphere(matrixStack, vertexConsumer, state.alpha/2f, state.radius/2f);

        matrixStack.pop();
    }

    private void renderSphere(MatrixStack matrixStack, VertexConsumer vertexConsumer, float alpha, float radius) {
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

                Vector3f v0 = getSphereDot(minPhi, minTheta, radius);
                Vector3f v1 = getSphereDot(minPhi, maxTheta, radius);
                Vector3f v2 = getSphereDot(maxPhi, maxTheta, radius);
                Vector3f v3 = getSphereDot(maxPhi, minTheta, radius);

                RenderUtil.renderQuad(matrixStack.peek().getPositionMatrix(), matrixStack.peek(), vertexConsumer,
                        v0, v1 ,v2 ,v3,
                        alpha, 1, 1, 1,LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        minU, maxU, minV, maxV);
            }
        }
    }

    private Vector3f getSphereDot(float phi, float theta, float radius) {
        float x = (float) (Math.sin(phi) * Math.cos(theta));
        float y = (float) Math.cos(phi);
        float z = (float) (Math.sin(phi) * Math.sin(theta));
        return new Vector3f(x, y, z).mul(radius);
    }

    @Override
    public void updateRenderState(ShockwaveSphereEntity entity, ShockwaveSpereEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.radius = MathHelper.lerp(tickDelta, entity.getPrevRadius(), entity.getCurrentRadius());
        float alpha = MathHelper.clamp(1f - (state.age < 3 ? 0 : state.age / LightningBreathEntity.MAX_AGE), 0f, 1f);
        state.alpha = MathHelper.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = state.alpha;
    }
}
