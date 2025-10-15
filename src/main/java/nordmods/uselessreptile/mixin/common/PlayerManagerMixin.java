package nordmods.uselessreptile.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin { //todo restoring
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void spawnHeadMountDragon(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci,
                                      @Local Optional<ReadView> optional) {
        //optional.ifPresent(nbt -> {
        //    ServerWorld serverWorld = player.getEntityWorld();
        //    EntityType.getEntityFromData(NbtReadView.create(UselessReptile.ERROR_REPORTER, serverWorld.getRegistryManager(), nbt.read("HeadMountDragon", NbtCompound.CODEC).orElse(new NbtCompound())), serverWorld, SpawnReason.LOAD).ifPresent(dragon -> {
        //        serverWorld.tryLoadEntity(dragon);
        //        dragon.setPosition(player.getEntityPos());
        //        if (player.getFirstPassenger() == null) dragon.startRiding(player, true, true);
        //    });
        //});
    }
}
