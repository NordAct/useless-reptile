package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.Moleclaw;

public class MoleclawRenderer extends URRideableDragonEntityRenderer<Moleclaw> {
    public MoleclawRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.25f;
    }
}
