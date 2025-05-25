package nordmods.sap.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import nordmods.sap.ServerModelOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract World getWorld();

    @Inject(method = "tick", at = @At("TAIL"))
    private void processServerAnimation(CallbackInfo ci) {
        if (!getWorld().isClient() && this instanceof ServerModelOwner<?> modelOwner) modelOwner.processServerAnimation();
    }
}
