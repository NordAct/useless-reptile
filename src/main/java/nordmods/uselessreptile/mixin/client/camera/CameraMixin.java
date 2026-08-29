package nordmods.uselessreptile.mixin.client.camera;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.client.config.URClientConfig;
import nordmods.uselessreptile.client.util.duck.PassengerCameraRollOwner;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    private Entity entity;

    @Shadow
    protected abstract void move(float forwards, float up, float right);

    @Shadow
    public abstract Vec3 position();

    @Shadow
    private Level level;

    @Shadow
    public abstract float yRot();

    @Shadow
    public abstract float xRot();

    @Unique
    private static final int ROUNDS = 50;

    @Unique
    private float zPassengerRotLerped;

    /// Offsets camera by specified in config amount
    @Inject(method = "alignWithEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(FFF)V", shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z")
            )
    )
    private void offsetCameraDistance(float partialTicks, CallbackInfo ci) {
        if (!URClientConfig.getConfig().enableCameraOffset) return;
        if (this.entity.getVehicle() instanceof URRideableDragonEntity dragonEntity) {
            float scale = entity instanceof LivingEntity livingEntity ? livingEntity.getScale() : 1;

            float distanceToCameraOffset = -URClientConfig.getConfig().cameraDistanceOffset * dragonEntity.getScale() * scale;
            float verticalOffset = URClientConfig.getConfig().cameraVerticalOffset * dragonEntity.getScale() * scale;
            float horizontalOffset = -URClientConfig.getConfig().cameraHorizontalOffset * dragonEntity.getScale() * scale;

            moveUntilCollision(distanceToCameraOffset, verticalOffset, horizontalOffset);
        }
    }

    @Unique
    private void moveUntilCollision(float x, float y, float z) { //ain't pretty, but it works
        float dx = x / ROUNDS;
        float dy = y / ROUNDS;
        float dz = z / ROUNDS;
        for (int i = 0; i < ROUNDS; ++i) {
            float posX = (float) ((i & 1) * 2 - 1);
            float posY = (float) ((i >> 1 & 1) * 2 - 1);
            float posZ = (float) ((i >> 2 & 1) * 2 - 1);
            Vec3 from = position().add(posX * 0.1F, posY * 0.1F, posZ * 0.1F);
            Vec3 to = from.add(new Vec3(-x, -y, -z).yRot(yRot() * Mth.DEG_TO_RAD).xRot(xRot() * Mth.DEG_TO_RAD));
            HitResult hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hitResult.getType() == HitResult.Type.MISS) move(dx, dy, dz);
            else {
                move(-dx, -dy, -dz);
                break;
            }
        }
    }



    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
    private float addCameraRoll(float angleZ) {
        if (!URClientConfig.getConfig().enableCameraRoll) return angleZ;
        if (entity.getVehicle() instanceof URRideableDragonEntity) {
            angleZ += zPassengerRotLerped;
        }
        return angleZ;
    }

    @Inject(method = "alignWithEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z")
            )
    )
    private void computeCameraRoll(float partialTicks, CallbackInfo ci) {
        if (entity instanceof PassengerCameraRollOwner passengerCameraRollOwner && entity.getVehicle() instanceof URRideableDragonEntity) {
            zPassengerRotLerped = passengerCameraRollOwner.useless_reptile$getZRot(partialTicks);
        } else zPassengerRotLerped = 0;
    }
}