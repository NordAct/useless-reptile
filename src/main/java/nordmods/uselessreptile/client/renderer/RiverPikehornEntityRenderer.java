package nordmods.uselessreptile.client.renderer;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.ItemInHandGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;

public class RiverPikehornEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonEntityRenderer<RiverPikehornEntity, R> {
    public RiverPikehornEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager);
        withRenderLayer(new ItemInHandGeoLayer<>(this, "main_hand", null) {
            @Override
            protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
                return List.of(new RenderData<>("main_hand", ItemDisplayContext.NONE, (bone, renderState2) -> Either.left((ItemStack) renderState2.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(EquipmentSlot.MAINHAND))));
            }

            @Override
            protected void submitItemStackRender(MatrixStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, OrderedRenderCommandQueue renderTasks,
                                              CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
                poseStack.push();
                //bone.transformToBone(poseStack);
                RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                //super.submitItemStackRender(poseStack, bone, stack, displayContext, renderState, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
                final ItemRenderState stackRenderState = new ItemRenderState();
                final MinecraftClient mc = MinecraftClient.getInstance();

                mc.getItemModelManager().clearAndUpdate(stackRenderState, stack, displayContext, mc.world, null, (int)(long)renderState.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, 0L) + displayContext.ordinal());
                stackRenderState.render(poseStack, renderTasks, packedLight, OverlayTexture.DEFAULT_UV, 0);

                poseStack.pop();
            }
        });
        shadowRadius = 0.4f;
    }
}
