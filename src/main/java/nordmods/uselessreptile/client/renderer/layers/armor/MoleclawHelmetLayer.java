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


public class MoleclawHelmetLayer extends URDragonArmorLayer<MoleclawEntity> {
    public MoleclawHelmetLayer(GeoRenderer<MoleclawEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    private static final Identifier DIAMOND = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_diamond_head.png");
    private static final Identifier GOLD = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_gold_head.png");
    private static final Identifier IRON = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_iron_head.png");
    private static final Identifier DIAMOND_NO_GLASS = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_diamond_head_no_glass.png");
    private static final Identifier GOLD_NO_GLASS = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_gold_head_no_glass.png");
    private static final Identifier IRON_NO_GLASS = new Identifier(UselessReptile.MODID, "textures/entity/moleclaw/armor/armor_iron_head_no_glass.png");

    @Override
    public void render(MatrixStack matrixStackIn, MoleclawEntity entity, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {
        Item head = entity.getEquippedStack(EquipmentSlot.HEAD).getItem();
        if (!(head instanceof DragonArmorItem)) return;
        super.render(matrixStackIn, entity, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        RenderLayer cameo = RenderLayer.getEntityCutout(UselessReptile.EMPTY_TEXTURE);

        if (URItems.MOLECLAW_HELMET_DIAMOND.equals(head)) cameo = RenderLayer.getEntityTranslucentCull(DIAMOND);
        else if (URItems.MOLECLAW_HELMET_GOLD.equals(head)) cameo = RenderLayer.getEntityTranslucentCull(GOLD);
        else if (URItems.MOLECLAW_HELMET_IRON.equals(head)) cameo = RenderLayer.getEntityTranslucentCull(IRON);
        else if (URItems.DRAGON_HELMET_DIAMOND.equals(head)) cameo = RenderLayer.getArmorCutoutNoCull(DIAMOND_NO_GLASS);
        else if (URItems.DRAGON_HELMET_GOLD.equals(head)) cameo = RenderLayer.getArmorCutoutNoCull(GOLD_NO_GLASS);
        else if (URItems.DRAGON_HELMET_IRON.equals(head)) cameo = RenderLayer.getArmorCutoutNoCull(IRON_NO_GLASS);

        matrixStackIn.push();
        getRenderer().reRender(getDefaultBakedModel(entity), matrixStackIn, bufferSource, entity, cameo,
                bufferSource.getBuffer(cameo), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
        matrixStackIn.pop();
    }
}
