package nordmods.uselessreptile.mixin.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.util.duck.LightningChaserSpawnTimer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LightningChaserSpawnTimer, HeadMountDragonOwner {
    @Unique private int lightningChaserSpawnCooldown = 0;
    @Unique private NbtCompound headMountDragon = new NbtCompound();
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void writeToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("LightningChaserSpawnCooldown", useless_reptile$getTimer());
        if (!headMountDragon.isEmpty()) nbt.put("HeadMountDragon", headMountDragon);
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void readFromNbt(NbtCompound nbt, CallbackInfo ci) {
        useless_reptile$setTimer(nbt.getInt("LightningChaserSpawnCooldown", URConfig.getConfig().lightningChaserThunderstormSpawnTimerCooldown));
        setHeadMountDragon(nbt.getCompoundOrEmpty("HeadMountDragon"));
    }

    public int useless_reptile$getTimer() {
        return lightningChaserSpawnCooldown;
    }
    public void useless_reptile$setTimer(int state) {
        lightningChaserSpawnCooldown = state;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickTimer(CallbackInfo ci) {
        if (useless_reptile$getTimer() > 0) useless_reptile$setTimer(useless_reptile$getTimer() - 1);
    }

    @Override
    public void setHeadMountDragon(@NotNull NbtCompound state) {
        headMountDragon = state;
    }

    @Override
    @NotNull
    public NbtCompound getHeadMountDragon() {
        return headMountDragon;
    }

    @Inject(method = "remove", at = @At("TAIL")) //TODO check if fix is working
    private void removeHeadMountDragon(RemovalReason reason, CallbackInfo ci) {
        if (!headMountDragon.isEmpty() && getWorld() instanceof ServerWorld world) {
            if (!reason.shouldDestroy()) {
                EntityType.getEntityFromNbt(headMountDragon, world, SpawnReason.LOAD).ifPresent(dragon -> {
                    dragon.remove(reason);
                });
                //Entity dragon = world.getEntity(headMountDragon.getUuid("UUID"));
                //if (dragon != null) dragon.remove(reason);
            }
        }
    }
}
