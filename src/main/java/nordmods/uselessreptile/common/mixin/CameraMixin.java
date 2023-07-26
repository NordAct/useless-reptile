package nordmods.uselessreptile.common.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class CameraMixin {

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V"))
    public void offset(Args args) {
        if (!URConfig.getConfig().enableCameraOffset) return;

        if (MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity) {
            args.set(1, URConfig.getConfig().cameraVerticalOffset);
            args.set(2, URConfig.getConfig().cameraHorizontalOffset);
        }
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(D)D"))
    public double offsetCameraDistance(double desiredCameraDistance) {
        if (!URConfig.getConfig().enableCameraOffset) return desiredCameraDistance;

        if (MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity) return desiredCameraDistance + URConfig.getConfig().cameraDistanceOffset;
        else return desiredCameraDistance;
    }

}
