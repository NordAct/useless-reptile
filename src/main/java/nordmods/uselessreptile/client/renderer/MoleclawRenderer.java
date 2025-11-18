package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.Moleclaw;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MoleclawRenderer<R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<Moleclaw, R> {
    public MoleclawRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.25f;
    }
}
