package nordmods.uselessreptile.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.client.util.duck.DragonPassengerOwner;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>{
    @Unique
    private static final Quaternionf EMPTY = new Quaternionf();

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"), cancellable = true)
    private void cancelRender(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntityRenderState instanceof DragonPassengerOwner owner && owner.isRidingDragon())
            if (DragonPassengerLayer.PASSENGERS.contains(owner.getUUID())) ci.cancel();
    }

    @ModifyArg(method = "setupTransforms",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;isInPose(Lnet/minecraft/entity/EntityPose;)Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V")))
    private Quaternionf undoRot(Quaternionf quaternion, @Local(ordinal = 0, argsOnly = true) S state) {
        if (!(state instanceof DragonPassengerOwner owner && owner.isRidingDragon())) return quaternion;
        return EMPTY;
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        if (!(livingEntityRenderState instanceof DragonPassengerOwner owner)) return;
        if (livingEntity.getVehicle() instanceof URRideableDragonEntity) {
            owner.setRidingDragon(true);
            owner.setUUID(livingEntity.getUuid());
        } else owner.setRidingDragon(false);
    }
}
