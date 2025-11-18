package nordmods.uselessreptile.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Unique private float prevStrength;
    @Shadow @Final private Minecraft minecraft;

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean render(CameraType instance, Operation<Boolean> original) {
        if (URClientConfig.getConfig().enableCrosshair && Minecraft.getInstance().player.getVehicle() instanceof URRideableDragonEntity) return true;
        return original.call(instance);
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z"))
    private void yeetShockEffect(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci, @Local LocalRef<Collection<MobEffectInstance>> localRef) {
        List<MobEffectInstance> copy = new ArrayList<>(List.copyOf(localRef.get()));
        copy.removeIf(statusEffectInstance -> statusEffectInstance.getEffect().equals(URStatusEffects.SHOCK));
        localRef.set(copy);
    }

    @Inject(method = "renderCameraOverlays", at = @At("TAIL"))
    private void renderShockOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (minecraft.player.hasEffect(URStatusEffects.SHOCK)) {
            float strength = Mth.clamp(minecraft.player.getEffect(URStatusEffects.SHOCK).getDuration()/100f, 0f, 1f);
            renderShockOverlay(context, strength, tickCounter.getGameTimeDeltaPartialTick(false));
            prevStrength = strength;
        } else prevStrength = 1f;
    }

    @Unique
    private void renderShockOverlay(GuiGraphics context, float strength, float tickDelta) {
        int width = context.guiWidth();
        int height = context.guiHeight();

        context.pose().pushMatrix();
        float scale = Mth.clamp(1.5f - Mth.lerp(tickDelta, prevStrength, strength), 1f, 2f);
        context.pose().translate(width/2f, height/2f);
        context.pose().scale(scale, scale);
        context.pose().translate(-width/2f, -height/2f);

        float r = 0.72f * strength;
        float g = 0.82f * strength;
        float b = 0.9f * strength;
        int color = ARGB.colorFromFloat(1.0F, r, g, b);
        context.blit(URRenderPipelines.GUI_SHOCK_OVERLAY, Gui.NAUSEA_LOCATION, 0, 0, 0, 0, width, height, width, height, color);

        context.pose().popMatrix();
    }
}
