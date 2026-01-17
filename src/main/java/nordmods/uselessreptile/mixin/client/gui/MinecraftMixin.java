package nordmods.uselessreptile.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    /// Forces player to open its own inventory if not controlling dragon
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isServerControlledInventory()Z"))
    private boolean openPlayerInventoryAnyway(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (player.getVehicle() instanceof URRideableDragonEntity dragon && !dragon.isOwnedBy(player)) {
            return false;
        }
        return original.call(instance);
    }
}
