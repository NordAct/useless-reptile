package nordmods.uselessreptile.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.RenderDispatcher;
import nordmods.uselessreptile.client.util.ShootingPointCommandRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderDispatcher.class)
public class RenderDispatcherMixin {
    @Shadow
    @Final
    private VertexConsumerProvider.Immediate vertexConsumers;
    @Unique private final ShootingPointCommandRenderer shootingPointCommandRenderer = new ShootingPointCommandRenderer();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/LayeredCustomCommandRenderer;render(Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;)V"))
    private void renderShootingPoins(CallbackInfo ci, @Local BatchingRenderCommandQueue batchingRenderCommandQueue) {
        shootingPointCommandRenderer.render(batchingRenderCommandQueue, vertexConsumers);
    }
}
