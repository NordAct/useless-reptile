package nordmods.uselessreptile.mixin.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.util.LightningChaserSpawnTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LightningChaserSpawnTimer {
    @Unique private int lightningChaserSpawnCooldown = 0;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void writeToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("LightningChaserSpawnCooldown", useless_reptile$getTimer());
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void readFromNbt(NbtCompound nbt, CallbackInfo ci) {
        useless_reptile$setTimer(nbt.getInt("LightningChaserSpawnCooldown"));
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
}
