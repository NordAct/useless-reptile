package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void setFirstPersonPerspective(CallbackInfo ci) {
        if (!URClientConfig.getConfig().autoThirdPerson) return;
        if (!(getVehicle() instanceof URRideableDragonEntity) || !((Object)this instanceof PlayerEntity player && player.isMainPlayer())) return;

        GameOptions gameOptions = MinecraftClient.getInstance().options;
        gameOptions.setPerspective(Perspective.FIRST_PERSON);
    }
}
