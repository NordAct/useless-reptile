package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class DragonMainHandItemLayer<T extends URDragonEntity> extends BlockAndItemGeoLayer<T> {

    public DragonMainHandItemLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Nullable
    @Override
    protected ItemStack getStackForBone(GeoBone bone, T animatable) {
        return bone.getName().equals("main_hand") ? animatable.getEquippedStack(EquipmentSlot.MAINHAND) : null;
    }

    @Override
    protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, T animatable, VertexConsumerProvider bufferSource,
                                      float partialTick, int packedLight, int packedOverlay) {
        poseStack.push();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.pop();
    }
}
