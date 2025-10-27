package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements HeadMountDragonOwner {
    @Unique private NbtCompound headMountDragon = new NbtCompound();
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeToNbt(WriteView view, CallbackInfo ci) {
        if (!headMountDragon.isEmpty()) view.put("HeadMountDragon", NbtCompound.CODEC, headMountDragon);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readFromNbt(ReadView view, CallbackInfo ci) {
        useless_reptile$setHeadMountDragon(view.read("HeadMountDragon", NbtCompound.CODEC).orElse(headMountDragon));
    }

    @Override
    public void useless_reptile$setHeadMountDragon(@NotNull NbtCompound state) {
        headMountDragon = state;
    }

    @Override
    @NotNull
    public NbtCompound useless_reptile$getHeadMountDragon() {
        return headMountDragon;
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void removeHeadMountDragon(RemovalReason reason, CallbackInfo ci) {
        if (!headMountDragon.isEmpty() && getEntityWorld() instanceof ServerWorld world) {
            if (!reason.shouldDestroy()) {
                EntityType.getEntityFromData(NbtReadView.create(UselessReptile.ERROR_REPORTER, world.getRegistryManager(), headMountDragon), world, SpawnReason.LOAD).ifPresent(dragon -> {
                    dragon.remove(reason);
                });
            }
        }
    }
}
