package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(FFF)V"))
    public void offset(Args args) {
        if (!URClientConfig.getConfig().enableCameraOffset) return;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player.getVehicle() instanceof URRideableDragonEntity dragonEntity) {
            args.set(1, URClientConfig.getConfig().cameraVerticalOffset * dragonEntity.getScale());
            args.set(2, -URClientConfig.getConfig().cameraHorizontalOffset * dragonEntity.getScale());
        }
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"))
    public float offsetCameraDistance(float desiredCameraDistance) {
        if (!URClientConfig.getConfig().enableCameraOffset) return desiredCameraDistance;

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player.getVehicle() instanceof URRideableDragonEntity dragonEntity)
            return desiredCameraDistance + URClientConfig.getConfig().cameraDistanceOffset  * dragonEntity.getScale();
        else return desiredCameraDistance;
    }

}
