package nordmods.uselessreptile.mixin.common.head_mount_dragon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.common.util.duck.HeadMountDragonOwner;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Saves head mount dragon to player
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements HeadMountDragonOwner {
    @Unique private CompoundTag headMountDragon = new CompoundTag();
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeToNbt(ValueOutput view, CallbackInfo ci) {
        if (!headMountDragon.isEmpty()) view.store("HeadMountDragon", CompoundTag.CODEC, headMountDragon);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readFromNbt(ValueInput view, CallbackInfo ci) {
        useless_reptile$setHeadMountDragon(view.read("HeadMountDragon", CompoundTag.CODEC).orElse(headMountDragon));
    }

    @Override
    public void useless_reptile$setHeadMountDragon(@NonNull CompoundTag state) {
        headMountDragon = state;
    }

    @Override
    @NonNull
    public CompoundTag useless_reptile$getHeadMountDragon() {
        return headMountDragon;
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void removeHeadMountDragon(RemovalReason reason, CallbackInfo ci) {
        if (!headMountDragon.isEmpty() && level() instanceof ServerLevel world) {
            if (!reason.shouldDestroy()) {
                EntityType.create(TagValueInput.create(UselessReptile.ERROR_REPORTER, world.registryAccess(), headMountDragon), world, EntitySpawnReason.LOAD).ifPresent(dragon -> dragon.remove(reason));
            }
        }
    }
}
