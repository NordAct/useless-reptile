package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class RiverPikehornEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonEntityRenderer<RiverPikehornEntity, R> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        addRenderLayer(new BlockAndItemGeoLayer<>(this,
                (bone ,state) -> {
                    if (!bone.getName().equals("main_hand")) return null;
                    GeoRenderState ownerState = state.getOrDefaultGeckolibData(URDataTickets.DRAGON_RENDER_STATE, null);
                    if (ownerState != null) return ownerState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT).get(EquipmentSlot.MAINHAND);
                    return null;
                },
                (bone, state) -> null) {
            @Override
            protected void renderStackForBone(MatrixStack poseStack, GeoBone bone, ItemStack stack, R renderState, VertexConsumerProvider bufferSource,
                                              int packedLight, int packedOverlay) {
                poseStack.push();
                poseStack.scale(0.5f, 0.5f, 0.5f);
                super.renderStackForBone(poseStack, bone, stack, renderState, bufferSource, packedLight, packedOverlay);
                poseStack.pop();            }
        });
        shadowRadius = 0.4f;
    }
}
