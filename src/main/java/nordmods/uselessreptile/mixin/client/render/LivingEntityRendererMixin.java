package nordmods.uselessreptile.mixin.client.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Some stuff for correct passenger rendering on passenger layer
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>{

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "HEAD"), cancellable = true)
    private void cancelRender(S livingEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (livingEntityRenderState.useless_reptile$isRidingDragon())
            if (DragonPassengerLayer.PASSENGERS.contains(livingEntityRenderState.useless_reptile$getUUID())) ci.cancel();
    }

    @ModifyArg(method = "setupRotations",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;hasPose(Lnet/minecraft/world/entity/Pose;)Z"),
                    to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;rotateDegrees(Lcom/mojang/math/Axis;F)V")),
            index = 1
    )
    private float undoRot(float angle, @Local(ordinal = 0, argsOnly = true) S state) {
        if (!state.useless_reptile$isRidingDragon()) return angle;
        return 0;
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        if (livingEntity.getVehicle() instanceof URRideableDragonEntity) {
            livingEntityRenderState.useless_reptile$setRidingDragon(true);
            livingEntityRenderState.useless_reptile$setUUID(livingEntity.getUUID());
        } else livingEntityRenderState.useless_reptile$setRidingDragon(false);
    }
}
