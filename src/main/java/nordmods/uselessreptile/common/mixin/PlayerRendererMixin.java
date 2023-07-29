package nordmods.uselessreptile.common.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import nordmods.uselessreptile.client.renderer.layers.RiverPikehornOnHeadFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<ClientPlayerEntity, PlayerEntityModel<ClientPlayerEntity>> {

    public PlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<ClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("TAIL"))
    private void addPikehornOnHead(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        addFeature(new RiverPikehornOnHeadFeature(this, ctx.getModelLoader()));
    }
}
