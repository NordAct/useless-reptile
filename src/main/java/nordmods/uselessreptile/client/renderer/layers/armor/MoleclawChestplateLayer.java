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
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import nordmods.uselessreptile.common.init.URItems;
import nordmods.uselessreptile.common.items.DragonArmorItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MoleclawChestplateLayer extends URDragonArmorLayer<MoleclawEntity> {
    public MoleclawChestplateLayer(GeoRenderer<MoleclawEntity> entityRendererIn) {
        super(entityRendererIn);
    }
    private static final Identifier DIAMOND = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_diamond_body.png");
    private static final Identifier GOLD = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_gold_body.png");
    private static final Identifier IRON = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_iron_body.png");

    @Override
    public void render(MatrixStack matrixStackIn, MoleclawEntity entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        Item chestplate = entity.getEquippedStack(EquipmentSlot.CHEST).getItem();
        if (!(chestplate instanceof DragonArmorItem)) return;
        super.render(matrixStackIn, entity, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        RenderLayer cameo = RenderLayer.getEntityCutout(UselessReptile.EMPTY_TEXTURE);

        if (URItems.DRAGON_CHESTPLATE_DIAMOND.equals(chestplate)) cameo = RenderLayer.getArmorCutoutNoCull(DIAMOND);
        else if (URItems.DRAGON_CHESTPLATE_GOLD.equals(chestplate)) cameo = RenderLayer.getArmorCutoutNoCull(GOLD);
        else if (URItems.DRAGON_CHESTPLATE_IRON.equals(chestplate)) cameo = RenderLayer.getArmorCutoutNoCull(IRON);

        matrixStackIn.push();
        getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
        matrixStackIn.pop();
    }
}
