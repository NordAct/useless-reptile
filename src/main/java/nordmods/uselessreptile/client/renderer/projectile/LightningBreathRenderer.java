package nordmods.uselessreptile.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.renderer.LightningChaserRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.projectile.LightningBreath;
import org.joml.Random;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

//reference: https://habr.com/ru/articles/230483/
public class LightningBreathRenderer extends EntityRenderer<LightningBreath, LightningBreathRenderer.LightningBreathEntityRenderState> {
    private static final Identifier TEXTURE = UselessReptile.id("textures/entity/lightning_breath/beam.png");
    public LightningBreathRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NonNull LightningBreathEntityRenderState createRenderState() {
        return new LightningBreathEntityRenderState();
    }

    @Override
    public void submit(LightningBreathEntityRenderState state, @NonNull PoseStack matrices, @NonNull SubmitNodeCollector queue, @NonNull CameraRenderState cameraState) {
        int length = state.length;
        if (length < 1) return;

        matrices.pushPose();
        matrices.translate(-state.x, -state.y, -state.z);

        float r = (state.color & 0xFF0000 >> 16) / 255f;
        float g = (state.color & 0x00FF00 >> 8) / 255f;
        float b = (state.color & 0x0000FF) / 255f;

        queue.submitCustomGeometry(matrices, RenderTypes.entityTranslucentEmissive(TEXTURE), ((pose, buffer) -> {
            for (LightningBreath.LightningBreathBolt lightningBreathBolt : state.lightningBreathBolts) {
                for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                    LightningBreath.LightningBreathBolt.Segment segment = lightningBreathBolt.segments.get(i);
                    renderSegment(segment, pose, buffer, 0.2f, -0.01f, r, g, b, state.alpha);
                }

                for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                    LightningBreath.LightningBreathBolt.Segment segment = lightningBreathBolt.segments.get(i);
                    renderSegment(segment, pose, buffer, 0.4f, 0.01f, r, g, b, state.alpha / 4f);
                }

                for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                    LightningBreath.LightningBreathBolt.Segment segment = lightningBreathBolt.segments.get(i);
                    renderSegment(segment, pose, buffer, 0.6f, 0.02f, r, g, b, state.alpha / 8f);
                }
            }
        }));
        matrices.popPose();
    }
    private void renderSegment(
            LightningBreath.LightningBreathBolt.Segment segment,
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float size, float lengthExtension,
            float red, float green, float blue, float alpha
    ) {
        Vector3f dir = new Vector3f().set(segment.endPoint()).sub(segment.startPoint());
        dir.normalize();
        Vector3f extraLength = new Vector3f(dir).mul(lengthExtension);

        Vector3f up = new Vector3f(0, 1, 0);
        if (Math.abs(dir.dot(up)) > 0.98f) {
            up.set(1, 0, 0);
        }
        Vector3f right = new Vector3f().set(dir).cross(up).normalize();
        up = new Vector3f().set(right).cross(dir).normalize();

        float halfSize = size/2f;

        Vector3f r = new Vector3f(right).mul(halfSize);
        Vector3f u = new Vector3f(up).mul(halfSize);

        Vector3f v0 = segment.isStartingSegment() ? segment.startPoint() : new Vector3f(segment.startPoint()).add(r).add(u).sub(extraLength);
        Vector3f v1 = segment.isStartingSegment() ? segment.startPoint() : new Vector3f(segment.startPoint()).add(r).sub(u).sub(extraLength);
        Vector3f v2 = segment.isStartingSegment() ? segment.startPoint() : new Vector3f(segment.startPoint()).sub(r).sub(u).sub(extraLength);
        Vector3f v3 = segment.isStartingSegment() ? segment.startPoint() : new Vector3f(segment.startPoint()).sub(r).add(u).sub(extraLength);

        Vector3f v4 = segment.isEndingSegment() ? segment.endPoint() : new Vector3f(segment.endPoint()).add(r).add(u).add(extraLength);
        Vector3f v5 = segment.isEndingSegment() ? segment.endPoint() : new Vector3f(segment.endPoint()).add(r).sub(u).add(extraLength);
        Vector3f v6 = segment.isEndingSegment() ? segment.endPoint() : new Vector3f(segment.endPoint()).sub(r).sub(u).add(extraLength);
        Vector3f v7 = segment.isEndingSegment() ? segment.endPoint() : new Vector3f(segment.endPoint()).sub(r).add(u).add(extraLength);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v0, v1, v5, v4,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v1, v2, v6, v5,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v2, v3, v7, v6,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v3, v0, v4, v7,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v0, v3, v2, v1,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);

        RenderUtil.renderQuad(pose.pose(), pose,
                buffer,
                v4, v5, v6, v7,
                alpha, red, green ,blue, LightCoordsUtil.FULL_BRIGHT,
                0, 1, 0, 1);
    }

    @Override
    public void extractRenderState(LightningBreath entity, LightningBreathEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        Entity owner = entity.getOwner();
        if (owner == null) {
            state.length = 0;
            return;
        }
        state.length = entity.getBeamLength();
        float alpha = Mth.clamp(1f - (state.ageInTicks < 3 ? 0 : state.ageInTicks / entity.getMaxAge()), 0f, 1f);
        state.alpha = Mth.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = state.alpha;
        state.color = entity.getColor();

        for (int i = 0; i < entity.lightningBreathBolts.length; i++) {
            if (entity.lightningBreathBolts[i] != null) continue;

            Random random = new Random();
            LightningBreath.LightningBreathBolt lightningBreathBolt = new LightningBreath.LightningBreathBolt();
            float offset = Math.clamp(state.length / 10f, 1, 4);
            Vector3f headPos = LightningChaserRenderer.headPos.get(owner.getUUID());
            if (headPos == null) return;
            Vector3f vec3d = entity.getLookAngle().scale(state.length).toVector3f();
            lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(headPos, vec3d.add(headPos)));
            for (int l = 0; l < 3; l++) {
                //do not the foreach unless you want to cause infinite loop
                int listSize = lightningBreathBolt.segments.size();
                for (int j = 0; j < listSize; j++) {
                    LightningBreath.LightningBreathBolt.Segment segment = lightningBreathBolt.segments.get(j);
                    lightningBreathBolt.segments.remove(segment);
                    Vector3f start = segment.startPoint();
                    Vector3f end = segment.endPoint();
                    Vector3f mid = new Vector3f(
                            (start.x + end.x) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.y + end.y) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.z + end.z) / 2f + random.nextFloat() * offset * 2f - offset);
                    lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(start, mid));
                    lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(mid, end));

                    if (random.nextFloat() < (0.7f/j)) {
                        Vector3f branchDir = new Vector3f(mid).sub(start).normalize();
                        branchDir.rotateAxis(10 * 0.7f + random.nextFloat() * 10 * 0.6f, branchDir.x, branchDir.y, branchDir.z);
                        float branchLength = start.distance(mid) * (0.6f + random.nextFloat() * 0.5f);

                        Vector3f branchEnd = new Vector3f(mid).add(branchDir.mul(branchLength));
                        lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(new Vector3f(mid), branchEnd));
                    }
                }
                offset /= 2f;
            }
            lightningBreathBolt.segments.forEach(s -> {
                if (lightningBreathBolt.segments.stream().noneMatch(s1 -> s1.startPoint().equals(s.endPoint()))) s.markEnding();
                if (lightningBreathBolt.segments.stream().noneMatch(s1 -> s1.endPoint().equals(s.startPoint()))) s.markStarting();
            });
            entity.lightningBreathBolts[i] = lightningBreathBolt;
        }

        state.lightningBreathBolts = entity.lightningBreathBolts;
    }

    @Override
    public boolean shouldRender(LightningBreath entity, Frustum culler, double camX, double camY, double camZ) {
        return true;
    }

    public static class LightningBreathEntityRenderState extends EntityRenderState {
        public int length;
        public float alpha = 1;
        public int color;
        public LightningBreath.LightningBreathBolt[] lightningBreathBolts = new LightningBreath.LightningBreathBolt[0];
    }
}

