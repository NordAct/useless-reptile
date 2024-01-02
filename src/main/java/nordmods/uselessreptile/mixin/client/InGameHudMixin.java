package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import nordmods.uselessreptile.client.init.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    private boolean render(Perspective instance) {
        return instance.isFirstPerson() || URClientConfig.getConfig().enableCrosshair && MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity;
    }
}
