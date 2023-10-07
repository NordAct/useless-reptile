package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public abstract class URDragonRenderer <T extends URDragonEntity> extends GeoEntityRenderer<T> {
    public URDragonRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> model, boolean hasBanner) {
        super(renderManager, model);
        addLayer(new URGlowingLayer<>(this,
                modelProvider::getTextureResource,
                modelProvider::getModelResource,
                RenderLayer::getEyes));
    }
}
