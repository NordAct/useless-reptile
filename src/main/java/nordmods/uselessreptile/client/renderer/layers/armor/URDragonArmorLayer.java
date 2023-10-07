package nordmods.uselessreptile.client.renderer.layers.armor;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public abstract class URDragonArmorLayer<T extends URDragonEntity> extends GeoLayerRenderer<T> {
    protected EquipmentSlot slot;

    protected URDragonArmorLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    public Identifier getTexturePath(DragonArmorItem item, String dragonID) {
        String itemID = item.toString().substring(item.toString().indexOf(":") + 1);
        return new Identifier(UselessReptile.MODID, "textures/entity/" + dragonID + "/armor/" + itemID + ".png");
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn,
                      T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        Item tail = entity.getEquippedStack(slot).getItem();
        if (tail instanceof DragonArmorItem armor) {
            RenderLayer cameo;

            Identifier id = getTexturePath(armor, entity.getDragonID());
            cameo = ResourceUtil.doesExist(id) ?
                    RenderLayer.getEntityTranslucent(id) : RenderLayer.getTranslucent();

            matrixStackIn.push();
            getRenderer().render(getEntityModel().getModel(getEntityModel().getModelResource(entity)),entity, partialTicks, cameo,
                    matrixStackIn, bufferIn, bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.DEFAULT_UV, 1f, 1f,
                    1f, 1f);
            matrixStackIn.pop();
        }
    }
}
