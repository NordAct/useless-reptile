package nordmods.uselessreptile.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.init.URRenderPipelines;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import nordmods.uselessreptile.common.init.URStatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique private float prevStrength;
    @Shadow @Final private MinecraftClient client;

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z"))
    private boolean render(Perspective instance, Operation<Boolean> original) {
        if (URClientConfig.getConfig().enableCrosshair && MinecraftClient.getInstance().player.getVehicle() instanceof URRideableDragonEntity) return true;
        return original.call(instance);
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private void yeetShockEffect(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local LocalRef<Collection<StatusEffectInstance>> localRef) {
        List<StatusEffectInstance> copy = new ArrayList<>(List.copyOf(localRef.get()));
        copy.removeIf(statusEffectInstance -> statusEffectInstance.getEffectType().equals(URStatusEffects.SHOCK));
        localRef.set(copy);
    }

    @Inject(method = "renderMiscOverlays", at = @At("TAIL"))
    private void renderShockOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.player.hasStatusEffect(URStatusEffects.SHOCK)) {
            float strength = MathHelper.clamp(client.player.getStatusEffect(URStatusEffects.SHOCK).getDuration()/100f, 0f, 1f);
            renderShockOverlay(context, strength, tickCounter.getTickProgress(false));
            prevStrength = strength;
        } else prevStrength = 1f;
    }

    @Unique
    private void renderShockOverlay(DrawContext context, float strength, float tickDelta) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        context.getMatrices().pushMatrix();
        float scale = MathHelper.clamp(1.5f - MathHelper.lerp(tickDelta, prevStrength, strength), 1f, 2f);
        context.getMatrices().translate(width/2f, height/2f);
        context.getMatrices().scale(scale, scale);
        context.getMatrices().translate(-width/2f, -height/2f);

        float r = 0.72f * strength;
        float g = 0.82f * strength;
        float b = 0.9f * strength;
        int color = ColorHelper.fromFloats(1.0F, r, g, b);
        context.drawTexture(URRenderPipelines.GUI_SHOCK_OVERLAY, InGameHud.NAUSEA_TEXTURE, 0, 0, 0, 0, width, height, width, height, color);

        context.getMatrices().popMatrix();
    }
}
