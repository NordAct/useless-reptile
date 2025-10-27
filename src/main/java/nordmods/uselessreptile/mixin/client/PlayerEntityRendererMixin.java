package nordmods.uselessreptile.mixin.client;

import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<A extends PlayerLikeEntity & ClientPlayerLikeEntity> extends LivingEntityRenderer<A, PlayerEntityRenderState, PlayerEntityModel> {

    private PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("TAIL"))
    private void addPikehornOnHead(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        addFeature(new HeadMountDragonFeatureRenderer(this));
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(A playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        playerEntityRenderState.useless_reptile$setHeadMountDragonRenderer(
                playerLikeEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? RenderUtil.getEntityRenderer(dragon)
                        : null);

        playerEntityRenderState.useless_reptile$setHeadMountDragonRenderState(
                playerLikeEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? (software.bernie.geckolib.renderer.base.GeoRenderState) playerEntityRenderState.useless_reptile$getHeadMountDragonRenderer().getAndUpdateRenderState(dragon, f)
                        : null);
    }
}
