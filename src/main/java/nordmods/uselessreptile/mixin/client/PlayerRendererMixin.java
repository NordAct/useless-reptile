package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

    private PlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("TAIL"))
    private void addPikehornOnHead(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        addFeature(new HeadMountDragonFeatureRenderer(this));
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!(playerEntityRenderState instanceof HeadMountDragonOwner owner)) return;

        owner.setHeadMountDragon(abstractClientPlayerEntity.getFirstPassenger() instanceof HeadMountDragon dragon ? dragon : null);
    }
}
