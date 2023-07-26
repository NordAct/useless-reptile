package nordmods.uselessreptile.common.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void renderPikehorn(AbstractClientPlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (player.getFirstPassenger() instanceof RiverPikehornEntity dragon) {
            matrixStack.push();
            URRideableDragonEntity.passengers.remove(dragon.getUuid());
            PlayerEntityRenderer render = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);
            ModelPart head = render.getModel().head;
            float rollDegrees = player.getRoll();
            float roll = (float) Math.toRadians(rollDegrees);
            float yawDegrees =  player.getHeadYaw();
            float pitchDegrees = player.getPitch();
            dragon.setYaw(yawDegrees);

            Vec3d rotX = getRotationVector(MathHelper.wrapDegrees(rollDegrees), MathHelper.wrapDegrees(yawDegrees - 90));
            Vec3d rotZ = getRotationVector(0, yawDegrees);

            float offsetX = head.pivotX / 16 + 0;
            float offsetY = head.pivotY / 16;
            float offsetZ = head.pivotZ / 16 - 0.1f;

            matrixStack.translate(0, 1.6f, 0);
            matrixStack.multiply(new Quaternionf().setAngleAxis(Math.toRadians(pitchDegrees), rotX.x, rotX.y, rotX.z));
            matrixStack.multiply(new Quaternionf().setAngleAxis(-roll, rotZ.x, rotZ.y, rotZ.z));
            matrixStack.translate(-offsetZ * (float) rotZ.x + offsetX * (float) rotZ.z,
                    -offsetY + Math.abs(Math.sin(Math.toRadians(pitchDegrees)) * 0.2),
                    -offsetZ * (float) rotZ.z - offsetX * (float) rotZ.x);

            renderEntity(dragon, g, matrixStack, vertexConsumerProvider, i);

            URRideableDragonEntity.passengers.add(dragon.getUuid());
            matrixStack.pop();
        }
    }

    private  <E extends Entity> void renderEntity(E entityIn, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLight) {
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

    private Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }
}
