package nordmods.uselessreptile.common.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onDismounted", at = @At("HEAD"))
    private void setFirstPersonPerspective(Entity vehicle, CallbackInfo ci) {
        if (!URConfig.getConfig().autoThirdPerson) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        GameOptions gameOptions = MinecraftClient.getInstance().options;

        if (vehicle instanceof URRideableDragonEntity) gameOptions.setPerspective(Perspective.FIRST_PERSON);
    }
}
