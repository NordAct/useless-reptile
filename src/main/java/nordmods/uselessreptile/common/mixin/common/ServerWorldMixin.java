package nordmods.uselessreptile.common.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import nordmods.uselessreptile.common.entity.multipart.WorldMultipartHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldMultipartHelper {
    @Inject(method = "getDragonPart(I)Lnet/minecraft/entity/Entity;", at = @At("RETURN"), cancellable = true)
    public void getURDragonParts(int id, CallbackInfoReturnable<Entity> cir) {
        Entity entity = cir.getReturnValue();
        if (entity == null) cir.setReturnValue(getPartMap().get(id));
    }
}
