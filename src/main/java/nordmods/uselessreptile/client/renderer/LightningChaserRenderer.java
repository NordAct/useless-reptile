package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.LightningChaser;

public class LightningChaserRenderer extends URRideableDragonEntityRenderer<LightningChaser> {

    public LightningChaserRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }
}
