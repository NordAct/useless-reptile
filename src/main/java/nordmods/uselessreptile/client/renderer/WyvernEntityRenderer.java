package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.WyvernEntityModel;
import nordmods.uselessreptile.client.renderer.layers.BannerLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonRiderLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonSaddleLayer;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class WyvernEntityRenderer extends GeoEntityRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernEntityModel());
        List<String> ignore = List.of("dragon");
        addRenderLayer(new DragonRiderLayer<>(this, 0, 2.85f, -0.05f, "front", 0, ignore));
        addRenderLayer(new DragonSaddleLayer<>(this));
        addRenderLayer(new BannerLayer<>(this));
        shadowRadius = 1.5f;
    }

    @Override
    public void render(WyvernEntity animatable, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource,
                       int packedLight) {
        updateSaddle(animatable);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    public void updateSaddle (WyvernEntity entity) {
        boolean hasRider = entity.hasControllingPassenger();
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
        model.getBone("ropes").ifPresent(c -> c.setHidden(!hasRider));
    }
}
