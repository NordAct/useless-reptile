package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.resource.ResourcePack;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//in theory those methods should be called once per resource reload
//but if something throws an error within methods - something gone wrong, but that's not my fault and problem
@Mixin(ResourceReloadLogger.class)
public abstract class ResourceReloadLoggerMixin {
    @Inject(method = "reload(Lnet/minecraft/client/resource/ResourceReloadLogger$ReloadReason;Ljava/util/List;)V", at = @At("TAIL"))
    private void updateStatusOnStart(ResourceReloadLogger.ReloadReason reason, List<ResourcePack> packs, CallbackInfo ci) {
        ResourceUtil.isResourceReloadFinished = false;
        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.getEntities().forEach(entity -> {
                if (entity instanceof URDragonEntity dragon) dragon.getAssetCache().cleanCache();
            });
        }
        URDragonEntity.SOUND_INFO_HOLDER.clear();
    }

    @Inject(method = "finish()V", at = @At("TAIL"))
    private void updateStatusOnFinish(CallbackInfo ci) {
        ResourceUtil.isResourceReloadFinished = true;
    }

}
