package nordmods.uselessreptile.client.renderer.layers.armor;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public abstract class URDragonArmorLayer<T extends URDragonEntity> extends GeoRenderLayer<T> {

    protected EquipmentSlot slot;

    protected URDragonArmorLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    public Identifier getTexturePath(DragonArmorItem item) {
        String itemID = item.toString().substring(item.toString().indexOf(":") + 1);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + renderer.getAnimatable().getDragonID() + "/armor/" + itemID + ".png");
    }

    @Override
    public void render(MatrixStack matrixStackIn, T entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        Item tail = entity.getEquippedStack(slot).getItem();
        if (tail instanceof DragonArmorItem armor) {
            super.render(matrixStackIn, entity, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            RenderLayer cameo;

            Identifier id = getTexturePath(armor);
            cameo = ResourceUtil.doesExist(id) ?
                    RenderLayer.getEntityTranslucent(id) : RenderLayer.getTranslucent();

            matrixStackIn.push();
            getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                    bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                    1, 1, 1, 1);
            matrixStackIn.pop();
        }
    }
}
