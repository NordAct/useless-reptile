package nordmods.uselessreptile.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.GuiGraphics;
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

    @Inject(method = "renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private void yeetShockEffect(GuiGraphics context, int mouseX, int mouseY, CallbackInfo ci, @Local LocalRef<Collection<MobEffectInstance>> localRef) {
        List<MobEffectInstance> copy = new ArrayList<>(List.copyOf(localRef.get()));
        copy.removeIf(statusEffectInstance -> statusEffectInstance.getEffect().equals(URMobEffect.SHOCK));
        localRef.set(copy);
    }
}
