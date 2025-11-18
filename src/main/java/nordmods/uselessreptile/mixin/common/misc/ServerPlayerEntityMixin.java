package nordmods.uselessreptile.mixin.common.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.mixin.common.lightning_chaser.PlayerEntityMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    private ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V", at = @At("TAIL"))
    private void copySemaData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        useless_reptile$setTimer(oldPlayer.useless_reptile$getTimer());
    }
}
