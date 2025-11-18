package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.MagmamuncherEntity;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MagmamuncherEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonEntityRenderer<MagmamuncherEntity, R> {
    public MagmamuncherEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 0.35f;
    }
}
