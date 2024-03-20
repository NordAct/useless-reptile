package nordmods.uselessreptile.client.renderer.layers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.entity.special.RenderOnlyEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreBakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;

public class DragonSaddleLayer <T extends URRideableDragonEntity> extends GeoRenderLayer<T> {

    public DragonSaddleLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        if (!(entity instanceof WyvernEntity)) return;
        matrixStackIn.push();
        renderSaddle(matrixStackIn, bufferSource, packedLight, entity, partialTick);
        buffer = bufferSource.getBuffer(renderType);
        matrixStackIn.pop();
    }

    private void renderSaddle(MatrixStack matrixStackIn, VertexConsumerProvider bufferSource, int packedLightIn, T entity, float partialTick) {
        ClientWorld world = MinecraftClient.getInstance().world;
        RenderOnlyEntity<URRideableDragonEntity> saddle = new RenderOnlyEntity<>(world);
        saddle.owner = entity;

        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super RenderOnlyEntity<URRideableDragonEntity>> render = manager.getRenderer(saddle);
        matrixStackIn.push();
        try {
            render.render(saddle, 0, partialTick, matrixStackIn, bufferSource, packedLightIn);
        } catch (Throwable throwable1) {
            throw new CrashException(CrashReport.create(throwable1, "Rendering entity in world"));
        }
        matrixStackIn.pop();
        saddle.discard();
    }
}
