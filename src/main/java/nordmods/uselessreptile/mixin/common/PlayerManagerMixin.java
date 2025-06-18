package nordmods.uselessreptile.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import nordmods.uselessreptile.UselessReptile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void spawnHeadMountDragon(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci,
                                      @Local Optional<ReadView> optional, @Local(ordinal = 1) ServerWorld serverWorld2) {
        optional.ifPresent(nbt -> {
            EntityType.getEntityFromData(NbtReadView.create(UselessReptile.ERROR_REPORTER, serverWorld2.getRegistryManager(), nbt.read("HeadMountDragon", NbtCompound.CODEC).orElse(new NbtCompound())), serverWorld2, SpawnReason.LOAD).ifPresent(dragon -> {
                serverWorld2.tryLoadEntity(dragon);
                dragon.setPosition(player.getPos());
                if (player.getFirstPassenger() == null) dragon.startRiding(player, true);
            });
        });
    }
}
