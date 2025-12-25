package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonRenderer;
import nordmods.uselessreptile.common.entity.Magmamuncher;

public class MagmamuncherRenderer extends HeadMountDragonRenderer<Magmamuncher> {
    public MagmamuncherRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 0.35f;
    }
}
