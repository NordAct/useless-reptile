package nordmods.uselessreptile.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.projectile.ShockwaveSphere;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class ShockwaveSphereRenderer extends EntityRenderer<ShockwaveSphere, ShockwaveSphereRenderer.ShockwaveSpereEntityRenderState> {
    private static final Identifier TEXTURE = UselessReptile.id("textures/entity/shockwave_sphere/shockwave.png");
    private static final int SPHERE_ROWS = 16;

    public ShockwaveSphereRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NonNull ShockwaveSpereEntityRenderState createRenderState() {
        return new ShockwaveSpereEntityRenderState();
    }

    @Override
    public void submit(ShockwaveSpereEntityRenderState state, PoseStack matrixStack, @NonNull SubmitNodeCollector commandQueue, @NonNull CameraRenderState cameraRenderState) {
        matrixStack.pushPose();

        RenderType layer = RenderTypes.entityTranslucentEmissive(TEXTURE, true);

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(state.alpha / 2f * 180f));
        renderSphere(matrixStack, layer, Mth.clamp(state.alpha - 0.2f, 0, 1), state.radius);
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-state.alpha / 1.5f * 180f));
        renderSphere(matrixStack, layer, Mth.clamp(state.alpha/1.5f - 0.1f, 0, 1), state.radius/1.5f);
        matrixStack.popPose();

        matrixStack.mulPose(Axis.YP.rotationDegrees(state.alpha * 180f));
        renderSphere(matrixStack, layer, state.alpha/2f, state.radius/2f);

        matrixStack.popPose();
    }

    private void renderSphere(PoseStack matrixStack, RenderType renderLayer, float alpha, float radius) {
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

                RenderUtil.renderQuad(matrixStack.last().pose(), matrixStack.last(), renderLayer,
                        v0, v1 ,v2 ,v3,
                        alpha, 1, 1, 1,LightTexture.FULL_BRIGHT,
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
    public void extractRenderState(ShockwaveSphere entity, ShockwaveSpereEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.radius = Mth.lerp(tickDelta, entity.getPrevRadius(), entity.getCurrentRadius());
        float alpha = Mth.clamp(1f - (state.ageInTicks < 3 ? 0 : state.radius / ShockwaveSphere.MAX_RADIUS), 0f, 1f);
        state.alpha = Mth.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = state.alpha;
    }

    public static class ShockwaveSpereEntityRenderState extends EntityRenderState {
        public float alpha = 1;
        public float radius;
    }
}
