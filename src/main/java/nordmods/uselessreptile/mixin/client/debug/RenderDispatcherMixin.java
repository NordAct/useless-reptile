package nordmods.uselessreptile.mixin.client.debug;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class RenderDispatcherMixin {
    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;
    @Unique private final ShootingPointCommandRenderer shootingPointCommandRenderer = new ShootingPointCommandRenderer();

    @Inject(method = "renderAllFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ParticleFeatureRenderer;render(Lnet/minecraft/client/renderer/SubmitNodeCollection;)V"))
    private void renderShootingPoins(CallbackInfo ci, @Local SubmitNodeCollection batchingRenderCommandQueue) {
        shootingPointCommandRenderer.render(batchingRenderCommandQueue, bufferSource);
    }
}
