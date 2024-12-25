package nordmods.uselessreptile.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffectInstance;
import nordmods.uselessreptile.common.init.URStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(StatusEffectsDisplay.class)
public abstract class StatusEffectsDisplayMixin {

    @Inject(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private void yeetShockEffect(DrawContext context, int mouseX, int mouseY, CallbackInfo ci, @Local LocalRef<Collection<StatusEffectInstance>> localRef) {
        List<StatusEffectInstance> copy = new ArrayList<>(List.copyOf(localRef.get()));
        copy.removeIf(statusEffectInstance -> statusEffectInstance.getEffectType().equals(URStatusEffects.SHOCK));
        localRef.set(copy);
    }
}
