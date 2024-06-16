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
import net.minecraft.item.Items;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DragonPassengerLayer<T extends DragonEquipmentAnimatable> extends GeoRenderLayer<T> {
    public static Set<UUID> passengers = new HashSet<>();
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
    public void renderForBone(MatrixStack matrixStackIn, T animatable, GeoBone bone, RenderLayer renderType,
                              VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.item != Items.SADDLE) return;
        if (!bone.getName().equals(passengerBone)) return;

        Entity passenger = animatable.owner.getPassengerList().size() > passengerNumber ? animatable.owner.getPassengerList().get(passengerNumber) : null;
        if (passenger != null) {
            matrixStackIn.push();
            passengers.remove(passenger.getUuid());

            Vec3d vec3d = passenger.getVehicleAttachmentPos(animatable.owner);
            matrixStackIn.translate(vec3d.x * 1/animatable.owner.getScale(), -vec3d.y * 1/animatable.owner.getScale(), vec3d.z * 1/animatable.owner.getScale());
            RenderUtil.translateToPivotPoint(matrixStackIn, bone);
            float yaw = MathHelper.lerpAngleDegrees(partialTick, animatable.owner.prevBodyYaw, animatable.owner.bodyYaw);
            matrixStackIn.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180f - yaw));
            matrixStackIn.scale(1/animatable.owner.getScale(), 1/animatable.owner.getScale(),1/animatable.owner.getScale());

            renderEntity(passenger, partialTick, matrixStackIn, bufferSource, packedLight);
            buffer = bufferSource.getBuffer(renderType);

            passengers.add(passenger.getUuid());
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
        try {
            render.render(entityIn, 0, partialTicks, matrixStack, bufferIn, packedLight);
        } catch (Throwable throwable1) {
            throw new CrashException(CrashReport.create(throwable1, "Rendering entity in world"));
        }
    }
}
