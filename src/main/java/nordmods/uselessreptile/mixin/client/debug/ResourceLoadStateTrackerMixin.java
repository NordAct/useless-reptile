package nordmods.uselessreptile.mixin.client.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.server.packs.PackResources;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//in theory those methods should be called once per resource reload
//but if something throws an error within methods - something gone wrong, but that's not my fault and problem

/// Captures start and stop of reload state
@Mixin(ResourceLoadStateTracker.class)
public abstract class ResourceLoadStateTrackerMixin {
    @Inject(method = "startReload(Lnet/minecraft/client/ResourceLoadStateTracker$ReloadReason;Ljava/util/List;)V", at = @At("TAIL"))
    private void updateStatusOnStart(ResourceLoadStateTracker.ReloadReason reason, List<PackResources> packs, CallbackInfo ci) {
        ResourceUtil.isResourceReloadFinished = false;
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.entitiesForRendering().forEach(entity -> {
                if (entity instanceof URDragonEntity dragon) dragon.getAssetCache().cleanCache();
            });
        }
        URDragonEntity.SOUND_INFO_HOLDER.clear();
    }

    @Inject(method = "finishReload()V", at = @At("TAIL"))
    private void updateStatusOnFinish(CallbackInfo ci) {
        ResourceUtil.isResourceReloadFinished = true;
    }

}
