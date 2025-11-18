package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void setFirstPersonPerspective(CallbackInfo ci) {
        if (!URClientConfig.getConfig().autoThirdPerson) return;
        if (!(getVehicle() instanceof URRideableDragonEntity) || !((Object)this instanceof Player player && player.isLocalPlayer())) return;

        Options gameOptions = Minecraft.getInstance().options;
        gameOptions.setCameraType(CameraType.FIRST_PERSON);
    }
}
