package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import nordmods.uselessreptile.client.init.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V"))
    public void offset(Args args) {
        if (!URClientConfig.getConfig().enableCameraOffset) return;

        if (MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity) {
            args.set(1, URClientConfig.getConfig().cameraVerticalOffset);
            args.set(2, URClientConfig.getConfig().cameraHorizontalOffset);
        }
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(D)D"))
    public double offsetCameraDistance(double desiredCameraDistance) {
        if (!URClientConfig.getConfig().enableCameraOffset) return desiredCameraDistance;

        if (MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity) return desiredCameraDistance + URClientConfig.getConfig().cameraDistanceOffset;
        else return desiredCameraDistance;
    }

}
