package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import nordmods.uselessreptile.client.renderer.*;
import nordmods.uselessreptile.client.renderer.projectile.AcidBlastRenderer;
import nordmods.uselessreptile.client.renderer.projectile.LightningBreathRenderer;
import nordmods.uselessreptile.client.renderer.projectile.ShockwaveSphereRenderer;
import nordmods.uselessreptile.common.init.UREntities;

@Environment(EnvType.CLIENT)
public class URRenderers {
    public static void init() {
        EntityRendererRegistry.register(UREntities.WYVERN_ENTITY, WyvernRenderer::new);
        EntityRendererRegistry.register(UREntities.MOLECLAW_ENTITY, MoleclawRenderer::new);
        EntityRendererRegistry.register(UREntities.RIVER_PIKEHORN_ENTITY, RiverPikehornRenderer::new);
        EntityRendererRegistry.register(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaserRenderer::new);
        EntityRendererRegistry.register(UREntities.MAGMAMUNCHER_ENTITY, MagmamuncherRenderer::new);

        EntityRendererRegistry.register(UREntities.ACID_BLAST_ENTITY, AcidBlastRenderer::new);
        EntityRendererRegistry.register(UREntities.SHOCKWAVE_SPHERE_ENTITY, ShockwaveSphereRenderer::new);
        EntityRendererRegistry.register(UREntities.LIGHTNING_BREATH_ENTITY, LightningBreathRenderer::new);
    }
}
