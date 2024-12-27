package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonRenderer;
import nordmods.uselessreptile.client.renderer.layers.DragonMainHandItemLayer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;

public class RiverPikehornEntityRenderer extends HeadMountDragonRenderer<RiverPikehornEntity> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        addRenderLayer(new DragonMainHandItemLayer<>(this));
        shadowRadius = 0.4f;
    }
}
