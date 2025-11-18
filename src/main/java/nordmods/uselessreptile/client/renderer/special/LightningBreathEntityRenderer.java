package nordmods.uselessreptile.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.renderer.LightningChaserEntityRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import org.joml.Random;
import org.joml.Vector3f;

//reference: https://habr.com/ru/articles/230483/
public class LightningBreathEntityRenderer extends EntityRenderer<LightningBreathEntity, LightningBreathEntityRenderer.LightningBreathEntityRenderState> {
    private static final ResourceLocation TEXTURE = UselessReptile.id("textures/entity/lightning_breath/beam.png");
    public LightningBreathEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public LightningBreathEntityRenderState createRenderState() {
        return new LightningBreathEntityRenderState();
    }

    @Override
    public void submit(LightningBreathEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        int length = state.length;
        if (length < 1) return;

        matrices.pushPose();
        for (LightningBreathEntity.LightningBreathBolt lightningBreathBolt : state.lightningBreathBolts)
            for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                LightningBreathEntity.LightningBreathBolt.Segment current = lightningBreathBolt.segments.get(i);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderType.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, 0.1f, 0),
                        new Vector3f(current.startPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.1f, 0),
                        state.alpha, 1, 1, 1, LightTexture.FULL_BRIGHT,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderType.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, -0.2f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.2f, 0),
                        state.alpha / 1.5f, 1, 1, 1, LightTexture.FULL_BRIGHT,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.last().pose(), matrices.last(),
                        RenderType.entityTranslucentEmissive(TEXTURE),
                        new Vector3f(current.startPoint()).add(0, -0.3f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.3f, 0),
                        state.alpha / 3f, 1, 1, 1, LightTexture.FULL_BRIGHT,
                        0, 1, 0, 1);
        }
        matrices.popPose();
    }

    public void updateRenderState(LightningBreathEntity entity, LightningBreathEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        Entity owner = entity.getOwner();
        if (owner == null) {
            state.length = 0;
            return;
        }
        state.length = entity.getBeamLength();
        float alpha = Mth.clamp(1f - (state.ageInTicks < 3 ? 0 : state.ageInTicks / LightningBreathEntity.MAX_AGE), 0f, 1f);
        state.alpha = Mth.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = state.alpha;

        for (int i = 0; i < entity.lightningBreathBolts.length; i++) {
            LightningBreathEntity.LightningBreathBolt lightningBreathBolt = entity.lightningBreathBolts[i];
            if (lightningBreathBolt != null) continue;

            lightningBreathBolt = new LightningBreathEntity.LightningBreathBolt();
            float offset = state.length / (8f + i);
            Vector3f headPos = LightningChaserEntityRenderer.headPos.get(owner.getUUID());
            if (headPos == null) return;
            //because actual owner's position and lightning breath's one are never the same, and we technically render lightning breath here...
            Vector3f startPos = new Vector3f((float) (owner.getX() - state.x), (float) (owner.getY() - state.y), (float) (owner.getZ() - state.z));
            startPos.add(headPos);
            Vector3f vec3d = entity.getLookAngle().scale(state.length).toVector3f();
            lightningBreathBolt.segments.add(new LightningBreathEntity.LightningBreathBolt.Segment(startPos, vec3d.add(startPos)));
            for (int l = 0; l < 3; l++) {
                //do not the foreach unless you want to cause infinite loop
                int listSize = lightningBreathBolt.segments.size();
                for (int j = 0; j < listSize; j++) {
                    LightningBreathEntity.LightningBreathBolt.Segment segment = lightningBreathBolt.segments.get(j);
                    lightningBreathBolt.segments.remove(segment);
                    Vector3f start = segment.startPoint();
                    Vector3f end = segment.endPoint();
                    Random random = new Random(l + owner.getRandom().nextInt(100));
                    Vector3f mid = new Vector3f(
                            (start.x + end.x) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.y + end.y) / 2f + random.nextFloat() * offset * 2f - offset,
                            (start.z + end.z) / 2f + random.nextFloat() * offset * 2f - offset);
                    lightningBreathBolt.segments.add(new LightningBreathEntity.LightningBreathBolt.Segment(start, mid));
                    lightningBreathBolt.segments.add(new LightningBreathEntity.LightningBreathBolt.Segment(mid, end));
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
        public LightningBreathEntity.LightningBreathBolt[] lightningBreathBolts = new LightningBreathEntity.LightningBreathBolt[0];
    }
}

