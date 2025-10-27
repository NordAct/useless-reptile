package nordmods.uselessreptile.mixin.common.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.init.URGameEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {
    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;)V"))
    private void emitLightningStrikeFarEvent(CallbackInfo ci) {
        emitGameEvent(URGameEvents.LIGHTNING_STRIKE_FAR);
    }
}
