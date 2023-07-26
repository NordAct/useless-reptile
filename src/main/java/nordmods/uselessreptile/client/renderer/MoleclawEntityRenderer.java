package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.MoleclawEntityModel;
import nordmods.uselessreptile.client.renderer.layers.*;
import nordmods.uselessreptile.client.renderer.layers.armor.MoleclawChestplateLayer;
import nordmods.uselessreptile.client.renderer.layers.armor.MoleclawHelmetLayer;
import nordmods.uselessreptile.client.renderer.layers.armor.MoleclawTailArmorLayer;
import nordmods.uselessreptile.common.entity.MoleclawEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class MoleclawEntityRenderer extends GeoEntityRenderer<MoleclawEntity> {
    public MoleclawEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MoleclawEntityModel());
        addRenderLayer(new MoleclawHelmetLayer(this));
        addRenderLayer(new MoleclawChestplateLayer(this));
        addRenderLayer(new MoleclawTailArmorLayer(this));
        addRenderLayer(new DragonRiderLayer<>(this, 0, 8.45f, -0.1f, "rider", 50, List.of("dragon")));
        addRenderLayer(new BannerLayer<>(this));
        shadowRadius = 1.25f;
    }

    @Override
    public void render(MoleclawEntity animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource,
                       int packedLight) {
        updateSaddle(animatable);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public void updateSaddle (MoleclawEntity entity) {
        boolean isSaddled = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("saddle_front").ifPresent(c -> c.setHidden(!isSaddled));
        model.getBone("saddle_neck").ifPresent(c -> c.setHidden(!isSaddled));
    }
}
