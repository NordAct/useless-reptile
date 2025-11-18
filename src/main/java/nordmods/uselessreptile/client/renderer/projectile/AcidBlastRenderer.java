package nordmods.uselessreptile.client.renderer.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import nordmods.uselessreptile.client.model.AcidBlastModel;
import nordmods.uselessreptile.common.entity.projectile.AcidBlast;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class AcidBlastRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<AcidBlast, R> {

    public AcidBlastRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AcidBlastModel());
    }

}
