package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDismounted", at = @At("HEAD"))
    private void setFirstPersonPerspective(Entity vehicle, CallbackInfo ci) {
        if (!URClientConfig.getConfig().autoThirdPerson) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        GameOptions gameOptions = MinecraftClient.getInstance().options;

        if (vehicle instanceof URRideableDragonEntity) gameOptions.setPerspective(Perspective.FIRST_PERSON);
    }

    //Fix for when ridden dragon (URideableDragonEntity) gets its velocity reset on client-side of other player. This is needed for correct animation playing
    @ModifyArg(method = "travelControlled", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private Vec3d clientVelocityResetFix(Vec3d par1) {
        if (getControllingPassenger() instanceof OtherClientPlayerEntity) return getVelocity();
        return par1;
    }
}
