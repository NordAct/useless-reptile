package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import nordmods.uselessreptile.client.model.WyvernProjectileEntityModel;
import nordmods.uselessreptile.common.entity.WyvernProjectileEntity;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;


public class WyvernProjectileEntityRenderer extends GeoProjectilesRenderer<WyvernProjectileEntity> {

    public WyvernProjectileEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernProjectileEntityModel());
    }

}
