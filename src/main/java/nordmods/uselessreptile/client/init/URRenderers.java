package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import net.minecraft.client.renderer.entity.NoopRenderer;
import nordmods.uselessreptile.client.renderer.*;
import nordmods.uselessreptile.client.renderer.projectile.AcidBlastRenderer;
import nordmods.uselessreptile.client.renderer.projectile.LightningBreathRenderer;
import nordmods.uselessreptile.client.renderer.projectile.ShockwaveSphereRenderer;
import nordmods.uselessreptile.common.init.UREntities;

@Environment(EnvType.CLIENT)
public class URRenderers {
    public static void init() {
        EntityRendererRegistryImpl.register(UREntities.WYVERN, WyvernRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.MOLECLAW, MoleclawRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.RIVER_PIKEHORN, RiverPikehornRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.LIGHTNING_CHASER, LightningChaserRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.MAGMAMUNCHER, MagmamuncherRenderer::new);

        EntityRendererRegistryImpl.register(UREntities.ACID_BLAST, AcidBlastRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.SHOCKWAVE_SPHERE, ShockwaveSphereRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.LIGHTNING_BREATH, LightningBreathRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.PLACEHOLDER, NoopRenderer::new);
    }
}
