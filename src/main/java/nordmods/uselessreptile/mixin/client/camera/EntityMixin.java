package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    /// Forces third person perspective when starting to ride dragon
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("TAIL"))
    private void setThirdPersonPerspective(Entity entity, boolean force, boolean emitEvent, CallbackInfoReturnable<Boolean> cir) {
        if (!URClientConfig.getConfig().autoThirdPerson) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.getVehicle() instanceof URRideableDragonEntity) Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }
}

