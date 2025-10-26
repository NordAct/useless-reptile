package nordmods.uselessreptile.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import nordmods.uselessreptile.UselessReptile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "net.minecraft.server.network.PrepareSpawnTask$PlayerSpawn")
public abstract class PrepareSpawnTaskMixin {
    @Inject(
            method = "onReady",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/server/network/ConnectedClientData;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void spawnHeadMountDragon(ClientConnection connection, ConnectedClientData clientData, CallbackInfoReturnable<ServerPlayerEntity> cir, @Local Optional<ReadView> optional, @Local ServerPlayerEntity player) {
        optional.ifPresent(nbt -> {
            ServerWorld serverWorld = player.getEntityWorld();
            EntityType.getEntityFromData(
                    NbtReadView.create(UselessReptile.ERROR_REPORTER,
                            serverWorld.getRegistryManager(),
                            nbt.read("HeadMountDragon", NbtCompound.CODEC).orElse(new NbtCompound())),
                    serverWorld, SpawnReason.LOAD)
                    .ifPresent(dragon -> {
                        serverWorld.tryLoadEntity(dragon);
                        dragon.setPosition(player.getEntityPos());
                        if (player.getFirstPassenger() == null) dragon.startRiding(player, true, true);
                    }
            );
        });
    }
}
