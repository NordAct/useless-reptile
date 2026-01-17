package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import nordmods.uselessreptile.UselessReptile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/// Spawns head mount dragon when player is loaded
@Mixin(targets = "net.minecraft.server.network.config.PrepareSpawnTask$Ready")
public abstract class PrepareSpawnTaskMixin {
    @Inject(
            method = "spawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void spawnHeadMountDragon(Connection connection, CommonListenerCookie clientData, CallbackInfoReturnable<ServerPlayer> cir, @Local Optional<ValueInput> optional, @Local ServerPlayer player) {
        optional.ifPresent(nbt -> {
            ServerLevel serverWorld = player.level();
            EntityType.create(
                    TagValueInput.create(UselessReptile.ERROR_REPORTER,
                            serverWorld.registryAccess(),
                            nbt.read("HeadMountDragon", CompoundTag.CODEC).orElse(new CompoundTag())),
                    serverWorld, EntitySpawnReason.LOAD)
                    .ifPresent(dragon -> {
                        serverWorld.addWithUUID(dragon);
                        dragon.setPos(player.position());
                        if (player.getFirstPassenger() == null) dragon.startRiding(player, true, true);
                    }
            );
        });
    }
}
