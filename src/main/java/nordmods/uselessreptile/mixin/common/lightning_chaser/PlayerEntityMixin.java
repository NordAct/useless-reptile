package nordmods.uselessreptile.mixin.common.lightning_chaser;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.uselessreptile.common.config.URConfig;
import nordmods.uselessreptile.common.util.duck.LightningChaserSpawnTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LightningChaserSpawnTimer {
    @Unique private int lightningChaserSpawnCooldown = 0;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeToNbt(ValueOutput view, CallbackInfo ci) {
        view.putInt("LightningChaserSpawnCooldown", useless_reptile$getTimer());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readFromNbt(ValueInput view, CallbackInfo ci) {
        useless_reptile$setTimer(view.getIntOr("LightningChaserSpawnCooldown", URConfig.getConfig().lightningChaserThunderstormSpawnTimerCooldown));
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
