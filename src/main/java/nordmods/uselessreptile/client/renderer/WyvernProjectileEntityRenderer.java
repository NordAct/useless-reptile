package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import nordmods.uselessreptile.client.model.WyvernProjectileEntityModel;
import nordmods.uselessreptile.common.entity.special.WyvernProjectileEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class WyvernProjectileEntityRenderer extends GeoEntityRenderer<WyvernProjectileEntity> {


    public WyvernProjectileEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernProjectileEntityModel());
    }

}
