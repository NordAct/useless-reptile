package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.client.model.riverpikehornonhead.RiverPikehornOnHeadModel;
import nordmods.uselessreptile.client.renderer.RiverPikehornEntityRenderer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;

public class RiverPikehornOnHeadFeature extends FeatureRenderer<ClientPlayerEntity, PlayerEntityModel<ClientPlayerEntity>> {

    private final RiverPikehornOnHeadModel model;
    public RiverPikehornOnHeadFeature(FeatureRendererContext<ClientPlayerEntity, PlayerEntityModel<ClientPlayerEntity>> context, EntityModelLoader loader) {
        super(context);
        model = new RiverPikehornOnHeadModel(loader.getModelPart(RiverPikehornOnHeadModel.PIKEHORN_ON_HEAD_LAYER));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.getFirstPassenger() instanceof RiverPikehornEntity dragon) {
            if (dragon.isInvisible()) return;
            matrices.push();
            getContextModel().head.rotate(matrices);
            model.setAngles(dragon, limbAngle, limbDistance, dragon.age, yaw(dragon.headYaw, entity.headYaw), MathHelper.clamp(dragon.getPitch() % 360 - entity.getPitch() % 360, -20, 20));
            model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getDragonTexture(dragon))), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
            matrices.pop();
        }
    }

    private Identifier getDragonTexture(RiverPikehornEntity dragon) {
        RiverPikehornEntityRenderer render;
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        render = (RiverPikehornEntityRenderer) manager.getRenderer(dragon);

        return render.getGeoModel().getTextureResource(dragon);
    }

    private float yaw(float entityYaw, float ownerYaw) {
        float a = entityYaw % 360;
        float b = ownerYaw % 360;
        if (b < 0) b += 360;
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal(entityYaw + ", " + ownerYaw + ", " + a +", " + b + ", " + (a - b)),false);
        return MathHelper.clamp(MathHelper.wrapDegrees(a - b), -45, 45);
    }
}
