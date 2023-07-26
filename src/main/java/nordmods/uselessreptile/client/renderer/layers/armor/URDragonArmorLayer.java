package nordmods.uselessreptile.client.renderer.layers.armor;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public abstract class URDragonArmorLayer<T extends URDragonEntity> extends GeoRenderLayer<T> {

    protected URDragonArmorLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }
}
