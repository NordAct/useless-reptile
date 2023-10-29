package nordmods.uselessreptile.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import nordmods.uselessreptile.client.model.river_pikehorn_on_head.RiverPikehornOnHeadModel;
import nordmods.uselessreptile.client.renderer.*;
import nordmods.uselessreptile.common.init.UREntities;

@Environment(EnvType.CLIENT)
public class URRenderers {
    public static void init() {
        EntityRendererRegistry.register(UREntities.WYVERN_ENTITY, WyvernEntityRenderer::new);
        EntityRendererRegistry.register(UREntities.MOLECLAW_ENTITY, MoleclawEntityRenderer::new);
        EntityRendererRegistry.register(UREntities.RIVER_PIKEHORN_ENTITY, RiverPikehornEntityRenderer::new);
        EntityRendererRegistry.register(UREntities.LIGHTNING_CHASER_ENTITY, LightningChaserEntityRenderer::new);

        EntityRendererRegistry.register(UREntities.WYVERN_PROJECTILE_ENTITY, WyvernProjectileEntityRenderer::new);
        EntityRendererRegistry.register(UREntities.SHOCKWAVE_SPHERE_ENTITY, ShockwaveSphereEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(RiverPikehornOnHeadModel.PIKEHORN_ON_HEAD_LAYER, RiverPikehornOnHeadModel::getTexturedModelData);
    }
}
