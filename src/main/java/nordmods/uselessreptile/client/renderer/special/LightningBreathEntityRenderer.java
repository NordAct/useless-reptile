package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.model.special.LightningBreathEntityModel;
import nordmods.uselessreptile.common.entity.special.LightningBreathEntity;

public class LightningBreathEntityRenderer extends EntityRenderer<LightningBreathEntity> {
    private final LightningBreathEntityModel model;
    private float prevAlpha = 0.7f;

    public LightningBreathEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        model = new LightningBreathEntityModel(ctx.getModelLoader().getModelPart(LightningBreathEntityModel.LIGHTNING_BREATH_MODEL));
    }

    @Override
    public Identifier getTexture(LightningBreathEntity entity) {
        return new Identifier(UselessReptile.MODID, "textures/entity/lightning_breath/beam.png");
    }

    @Override
    public void render(LightningBreathEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        int beamLength = entity.getBeamLength();
        if (beamLength < 1) return;

        float alpha = MathHelper.clamp(1f - (entity.getAge() < 3 ? 0 : (float) entity.getAge() / entity.maxAge), 0f, 1f);
        alpha = MathHelper.lerp(tickDelta, prevAlpha, alpha);
        prevAlpha = alpha;
        matrices.push();
        float scale = 1 + (1 - alpha)/4f;
        matrices.scale(scale, scale, scale);
        matrices.translate(0, -scale, 0);

        ModelPart toHide = model.getPart();
        for (int i = 1; i <= beamLength; i++) {
            toHide.visible = true;
            if (i >= LightningBreathEntity.MAX_LENGTH) {
                toHide = null;
                break;
            }
            toHide = toHide.getChild("beam" + (i + 1));
        }
        if (toHide != null) toHide.visible = false;

        model.setAngles(entity, 0, 0, entity.getAge(), (-entity.getYaw() + 180) % 360, -entity.getPitch()  % 360);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(getTexture(entity))), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 1, 1, 1, alpha);
        matrices.pop();
    }
}

