package nordmods.uselessreptile.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.world.effect.MobEffectInstance;
import nordmods.uselessreptile.common.init.URMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(EffectsInInventory.class)
public abstract class EffectsInInventoryMixin {
    /// Removes shock effect from displayed status effect list
    @Inject(method = "extractEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getFont()Lnet/minecraft/client/gui/Font;"))
    private void yeetShockEffect(GuiGraphicsExtractor guiGraphics, Collection<MobEffectInstance> collection, int i, int j, int k, int l, int m, CallbackInfo ci, @Local LocalRef<Collection<MobEffectInstance>> localRef) {
        List<MobEffectInstance> copy = new ArrayList<>(List.copyOf(localRef.get()));
        copy.removeIf(statusEffectInstance -> statusEffectInstance.getEffect().equals(URMobEffect.SHOCK));
        localRef.set(copy);
    }
}
