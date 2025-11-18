package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonRenderer;
import nordmods.uselessreptile.common.entity.Magmamuncher;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class MagmamuncherRenderer<R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonRenderer<Magmamuncher, R> {
    public MagmamuncherRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 0.35f;
    }
}
