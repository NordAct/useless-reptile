package nordmods.uselessreptile.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
        for (LightningBreath.LightningBreathBolt lightningBreathBolt : state.lightningBreathBolts)
            for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                LightningBreath.LightningBreathBolt.Segment current = lightningBreathBolt.segments.get(i);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderTypes.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, 0.1f, 0),
                        new Vector3f(current.startPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.1f, 0),
                        state.alpha, 1, 1, 1, LightCoordsUtil.FULL_BRIGHT,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderTypes.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, -0.2f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.2f, 0),
                        state.alpha / 1.5f, 1, 1, 1, LightCoordsUtil.FULL_BRIGHT,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderTypes.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, -0.3f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.3f, 0),
                        state.alpha / 3f, 1, 1, 1, LightCoordsUtil.FULL_BRIGHT,
                        0, 1, 0, 1);
        }
        matrices.popPose();
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

        for (int i = 0; i < entity.lightningBreathBolts.length; i++) {
            LightningBreath.LightningBreathBolt lightningBreathBolt = entity.lightningBreathBolts[i];
            if (lightningBreathBolt != null) continue;

            lightningBreathBolt = new LightningBreath.LightningBreathBolt();
            float offset = state.length / (8f + i);
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
                    Random random = new Random(l + owner.getRandom().nextInt(100));
                    Vector3f mid = new Vector3f(
                            (start.x + end.x) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.y + end.y) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.z + end.z) / 2f + random.nextFloat() * offset * 2f - offset);
                    lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(start, mid));
                    lightningBreathBolt.segments.add(new LightningBreath.LightningBreathBolt.Segment(mid, end));
                }
                offset /= 2f;
            }
            entity.lightningBreathBolts[i] = lightningBreathBolt;
        }

        state.lightningBreathBolts = entity.lightningBreathBolts;
    }

    public static class LightningBreathEntityRenderState extends EntityRenderState {
        public int length;
        public float alpha = 1;
        public LightningBreath.LightningBreathBolt[] lightningBreathBolts = new LightningBreath.LightningBreathBolt[0];
    }
}

