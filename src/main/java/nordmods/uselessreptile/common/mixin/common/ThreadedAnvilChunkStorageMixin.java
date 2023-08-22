package nordmods.uselessreptile.common.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    //I mean... EnderDragonPart also does this. Tbf idk what difference making this does, but I did it anyway
    @Inject(method = "loadEntity(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void cancelURDragonPartLoad(Entity entity, CallbackInfo ci) {
        if (entity instanceof URDragonPart) ci.cancel();
    }
}
