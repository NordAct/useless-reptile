package nordmods.uselessreptile.mixin.common.lightning_chaser;

import net.minecraft.entity.LivingEntity;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void forgiveSurrendered(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LightningChaserEntity lightningChaser && lightningChaser.hasSurrendered()) cir.setReturnValue(false);
    }
}
