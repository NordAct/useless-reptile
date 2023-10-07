package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URConfig;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.LayerGlowingAreasGeo;

import java.util.function.Function;

public class URGlowingLayer <T extends URDragonEntity> extends LayerGlowingAreasGeo<T> {
    public URGlowingLayer(GeoEntityRenderer<T> renderer, Function<T, Identifier> currentTextureFunction, Function<T, Identifier> currentModelFunction, Function<Identifier, RenderLayer> emissiveRenderTypeFunction) {
        super(renderer, currentTextureFunction, currentModelFunction, emissiveRenderTypeFunction);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                       float headPitch)  {
        if (!ResourceUtil.isResourceReloadFinished) return;
        if (!URConfig.getConfig().disableEmissiveTextures) {
            Identifier id = getEntityModel().getTextureResource(entity);
            id = new Identifier(UselessReptile.MODID, id.getPath() + ".mcmeta");
            if (ResourceUtil.doesExist(id)) super.render(matrixStackIn,bufferSource, packedLight , entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
