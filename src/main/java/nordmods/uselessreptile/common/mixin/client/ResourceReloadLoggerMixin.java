package nordmods.uselessreptile.common.mixin.client;

import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.resource.ResourcePack;
import nordmods.uselessreptile.client.util.ResourceUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ResourceReloadLogger.class)
public abstract class ResourceReloadLoggerMixin {
    @Shadow @Nullable
    private ResourceReloadLogger.ReloadState reloadState;

    @Inject(method = "reload(Lnet/minecraft/client/resource/ResourceReloadLogger$ReloadReason;Ljava/util/List;)V", at = @At("TAIL"))
    private void updateStatusOnStart(ResourceReloadLogger.ReloadReason reason, List<ResourcePack> packs, CallbackInfo ci) {
        if (reloadState != null) ResourceUtil.isResourceReloadFinished = reloadState.finished;
    }

    @Inject(method = "finish()V", at = @At("TAIL"))
    private void updateStatusOnFinish(CallbackInfo ci) {
        if (reloadState != null) ResourceUtil.isResourceReloadFinished = reloadState.finished;
    }

}
