package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import nordmods.uselessreptile.client.model.AcidBlastEntityModel;
import nordmods.uselessreptile.common.entity.special.AcidBlastEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class AcidBlastEntityRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<AcidBlastEntity, R> {

    public AcidBlastEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AcidBlastEntityModel());
    }

}
