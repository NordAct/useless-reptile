package nordmods.uselessreptile.mixin.client.render;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import nordmods.uselessreptile.client.renderer.special.HeadMountDragonFeatureRenderer;
import nordmods.uselessreptile.client.util.RenderUtil;
import nordmods.uselessreptile.common.entity.base.HeadMountDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin<A extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<A, AvatarRenderState, PlayerModel> {

    private PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("TAIL"))
    private void addPikehornOnHead(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        addLayer(new HeadMountDragonFeatureRenderer(this));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void checkForHeadMountDragon(A playerLikeEntity, AvatarRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        playerEntityRenderState.useless_reptile$setHeadMountDragonRenderer(
                playerLikeEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? RenderUtil.getEntityRenderer(dragon)
                        : null);

        playerEntityRenderState.useless_reptile$setHeadMountDragonRenderState(
                playerLikeEntity.getFirstPassenger() instanceof URDragonEntity dragon && dragon instanceof HeadMountDragon
                        ? (software.bernie.geckolib.renderer.base.GeoRenderState) playerEntityRenderState.useless_reptile$getHeadMountDragonRenderer().createRenderState(dragon, f)
                        : null);
    }
}
