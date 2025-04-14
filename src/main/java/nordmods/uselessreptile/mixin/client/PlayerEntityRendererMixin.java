package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.client.util.duck.HeadMountDragonOwner;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

    private PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("TAIL"))
    private void addPikehornOnHead(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        addFeature(new HeadMountDragonFeatureRenderer(this));
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!(playerEntityRenderState instanceof HeadMountDragonOwner owner)) return;
        owner.setHeadMountDragonRenderer(
                abstractClientPlayerEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? RenderUtil.getEntityRenderer(dragon)
                        : null);

        owner.setHeadMountDragonRenderState(
                abstractClientPlayerEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? (software.bernie.geckolib.renderer.base.GeoRenderState) owner.getHeadMountDragonRenderer().getAndUpdateRenderState(dragon, f)
                        : null);
    }
}
