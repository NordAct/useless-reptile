package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MoleclawEntityRenderer<T extends URDragonEntity, R extends LivingEntityRenderState & GeoRenderState> extends URRideableDragonEntityRenderer<MoleclawEntity, R> {
    public MoleclawEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.25f;
    }
}
