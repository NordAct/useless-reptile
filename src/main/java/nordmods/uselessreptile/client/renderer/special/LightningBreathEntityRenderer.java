package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.renderer.LightningChaserEntityRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;
import org.joml.Random;
import org.joml.Vector3f;

//reference: https://habr.com/ru/articles/230483/
public class LightningBreathEntityRenderer extends EntityRenderer<LightningBreathEntity> {
    public LightningBreathEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(LightningBreathEntity entity) {
        return UselessReptile.id("textures/entity/lightning_breath/beam.png");
    }

    @Override
    public void render(LightningBreathEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        int length = entity.getBeamLength();
        if (length < 1) return;

        for (int i = 0; i < entity.lightningBreathBolts.length; i++) {
            LightningBreathEntity.LightningBreathBolt lightningBreathBolt = entity.lightningBreathBolts[i];
            if (lightningBreathBolt == null) {
                if (!(entity.getOwner() instanceof LightningChaserEntity owner)) return;
                lightningBreathBolt = new LightningBreathEntity.LightningBreathBolt();
                float offset = length / (4f + i * 2);
                Vector3f headPos = LightningChaserEntityRenderer.headPos.get(owner.getUuid());
                if (headPos == null) return;
                //because actual owner's position and lightning breath's one are never the same, and we technically render lightning breath here...
                Vector3f startPos = new Vector3f((float) (owner.getX() - entity.getX()), (float) (owner.getY() - entity.getY()), (float) (owner.getZ() - entity.getZ()));
                startPos.add(headPos);
                Vector3f vec3d = owner.getRotationVector().multiply(length).toVector3f();
                lightningBreathBolt.segments.add(
                        new LightningBreathEntity.LightningBreathBolt.Segment(
                                new Vector3f(startPos.x, startPos.y, startPos.z),
                                new Vector3f(vec3d.x + startPos.x, vec3d.y + startPos.y, vec3d.z + startPos.x)));
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
        }

        float alpha = MathHelper.clamp(1f - (entity.getAge() < 3 ? 0 : (float) entity.getAge() / LightningBreathEntity.MAX_AGE), 0f, 1f);
        alpha = MathHelper.lerp(tickDelta, entity.prevAlpha, alpha);
        entity.prevAlpha = alpha;
        matrices.push();
        for (LightningBreathEntity.LightningBreathBolt lightningBreathBolt : entity.lightningBreathBolts)
            for (int i = 0; i < lightningBreathBolt.segments.size(); i++) {
                LightningBreathEntity.LightningBreathBolt.Segment current = lightningBreathBolt.segments.get(i);
                RenderUtil.renderQuad(matrices.peek().getPositionMatrix(), matrices.peek(),
                        vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity))),
                        new Vector3f(current.startPoint()).add(0, 0.1f, 0),
                        new Vector3f(current.startPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.1f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.1f, 0),
                        alpha, 1, 1, 1, LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.peek().getPositionMatrix(), matrices.peek(),
                        vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity))),
                        new Vector3f(current.startPoint()).add(0, -0.2f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.2f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.2f, 0),
                        alpha / 1.5f, 1, 1, 1, LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        0, 1, 0, 1);
                RenderUtil.renderQuad(matrices.peek().getPositionMatrix(), matrices.peek(),
                        vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity))),
                        new Vector3f(current.startPoint()).add(0, -0.3f, 0),
                        new Vector3f(current.startPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, 0.3f, 0),
                        new Vector3f(current.endPoint()).add(0, -0.3f, 0),
                        alpha / 3f, 1, 1, 1, LightmapTextureManager.MAX_LIGHT_COORDINATE,
                        0, 1, 0, 1);
        }
        matrices.pop();
    }

}

