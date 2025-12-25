package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.rendering.EntityRendererRegistryImpl;
import nordmods.uselessreptile.client.renderer.*;
import nordmods.uselessreptile.client.renderer.projectile.AcidBlastRenderer;
import nordmods.uselessreptile.client.renderer.projectile.LightningBreathRenderer;
import nordmods.uselessreptile.client.renderer.projectile.ShockwaveSphereRenderer;
import nordmods.uselessreptile.common.init.UREntities;

@Environment(EnvType.CLIENT)
public class URRenderers {
    public static void init() {
        EntityRendererRegistryImpl.register(UREntities.WYVERN_ENTITY, WyvernRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.MOLECLAW_ENTITY, MoleclawRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.RIVER_PIKEHORN_ENTITY, RiverPikehornRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaserRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.MAGMAMUNCHER_ENTITY, MagmamuncherRenderer::new);

        EntityRendererRegistryImpl.register(UREntities.ACID_BLAST_ENTITY, AcidBlastRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.SHOCKWAVE_SPHERE_ENTITY, ShockwaveSphereRenderer::new);
        EntityRendererRegistryImpl.register(UREntities.LIGHTNING_BREATH_ENTITY, LightningBreathRenderer::new);
    }
}
