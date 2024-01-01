package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.RotationAxis;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

public class DragonPassengerLayer<T extends URRideableDragonEntity> extends GeoRenderLayer<T> {
    private final String passengerBone;
    private final int passengerNumber;

    public DragonPassengerLayer(GeoRenderer<T> entityRendererIn, String passengerBone, int passengerNumber) {
        super(entityRendererIn);
        this.passengerBone = passengerBone;
        this.passengerNumber = passengerNumber;
    }

    public DragonPassengerLayer(GeoRenderer<T> entityRendererIn, String passengerBone) {
        this(entityRendererIn, passengerBone, 0);
    }

    @Override
    public void renderForBone(MatrixStack matrixStackIn, T entity, GeoBone bone, RenderLayer renderType,
                              VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!bone.getName().equals(passengerBone)) return;

        Entity passenger = entity.getPassengerList().size() > passengerNumber ? entity.getPassengerList().get(passengerNumber) : null;
        if (passenger != null) {
            matrixStackIn.push();
            URRideableDragonEntity.passengers.remove(passenger.getUuid());

            matrixStackIn.translate(0, -0.7f, 0);
            RenderUtils.translateToPivotPoint(matrixStackIn, bone);
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw(partialTick) - 180));
            renderEntity(passenger, partialTick, matrixStackIn, bufferSource, packedLight);
            buffer = bufferSource.getBuffer(renderType);

            URRideableDragonEntity.passengers.add(passenger.getUuid());
            matrixStackIn.pop();
        }
    }


    public <E extends Entity> void renderEntity(E entityIn, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
        boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (isFirstPerson && entityIn == clientPlayer) return;

        EntityRenderer<? super E> render;
        EntityRenderDispatcher manager = MinecraftClient.getInstance().getEntityRenderDispatcher();

        render = manager.getRenderer(entityIn);
        matrixStack.push();
        try {
            render.render(entityIn, 0, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new CrashException(CrashReport.create(throwable1, "Rendering entity in world"));
        }
        matrixStack.pop();
    }
}
