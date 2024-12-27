package nordmods.uselessreptile.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void spawnHeadMountDragon(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci,
                                      @Local Optional<NbtCompound> optional, @Local(ordinal = 1) ServerWorld serverWorld2) {
        optional.ifPresent(nbt -> {
            if (nbt.contains("HeadMountDragon", NbtElement.COMPOUND_TYPE)) {
                EntityType.getEntityFromNbt(nbt.getCompound("HeadMountDragon"), serverWorld2, SpawnReason.LOAD).ifPresent(dragon -> {
                    serverWorld2.tryLoadEntity(dragon);
                    dragon.setPosition(player.getPos());
                    if (player.getFirstPassenger() == null) dragon.startRiding(player, true);
                });
            }
        });
    }
}
