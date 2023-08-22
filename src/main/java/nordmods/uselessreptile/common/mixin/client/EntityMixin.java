package nordmods.uselessreptile.common.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("TAIL"))
    private void setThirdPersonPerspective(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (!URConfig.getConfig().autoThirdPerson) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        if (player.getVehicle() instanceof URRideableDragonEntity) MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }
}

