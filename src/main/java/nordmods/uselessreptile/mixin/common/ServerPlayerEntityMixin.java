package nordmods.uselessreptile.mixin.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.util.LightningChaserSpawnTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin{
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("TAIL"))
    private void copySemaData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        LightningChaserSpawnTimer oldData = (LightningChaserSpawnTimer) oldPlayer;
        setTimer(oldData.getTimer());
    }
}
