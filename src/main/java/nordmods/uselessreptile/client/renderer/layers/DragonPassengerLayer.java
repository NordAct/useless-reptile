package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DragonPassengerLayer<T extends DragonEquipmentAnimatable> extends GeoRenderLayer<T> {
    public static final Set<UUID> PASSENGERS = new HashSet<>();
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
        if (!bone.getName().equals(passengerBone)) return;

        Entity passenger = animatable.owner.getPassengerList().size() > passengerNumber ? animatable.owner.getPassengerList().get(passengerNumber) : null;
        if (passenger != null) {
            matrixStackIn.push();
            PASSENGERS.remove(passenger.getUuid());

            Vec3d vec3d = passenger.getVehicleAttachmentPos(animatable.owner);
            float scale = 1/animatable.owner.getScale();
            matrixStackIn.translate(vec3d.x * scale, -vec3d.y * scale, vec3d.z * scale);
            RenderUtil.translateToPivotPoint(matrixStackIn, bone);
            matrixStackIn.scale(scale, scale, scale);

            renderPassenger(passenger, partialTick, matrixStackIn, bufferSource, packedLight);

            PASSENGERS.add(passenger.getUuid());
            matrixStackIn.pop();
        }
    }


    private <E extends Entity> void renderPassenger(E entityIn, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
        boolean isFirstPerson = MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (entityIn == clientPlayer) {
            if (isFirstPerson || !URClientConfig.getConfig().renderPassengers.canRenderSelf()) return;
        } else if (!URClientConfig.getConfig().renderPassengers.canRenderOthers()) return;

        nordmods.uselessreptile.client.util.RenderUtil.renderEntity(entityIn, partialTicks, matrixStack, bufferIn, packedLight);
    }
}