package nordmods.uselessreptile.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.RiverPikehornEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.ItemInHandGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RiverPikehornEntityRenderer <R extends LivingEntityRenderState & GeoRenderState> extends HeadMountDragonEntityRenderer<RiverPikehornEntity, R> {
    public RiverPikehornEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        withRenderLayer(new ItemInHandGeoLayer<>(this, "main_hand", null) {
            @Override
            protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
                return List.of(new RenderData<>("main_hand", ItemDisplayContext.NONE, (bone, renderState2) -> Either.left((ItemStack) renderState2.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(EquipmentSlot.MAINHAND))));
            }

            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext displayContext, R renderState, SubmitNodeCollector renderTasks,
                                              CameraRenderState cameraState, int packedLight, int packedOverlay, int renderColor) {
                poseStack.pushPose();
                //bone.transformToBone(poseStack);
                RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                //super.submitItemStackRender(poseStack, bone, stack, displayContext, renderState, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
                final ItemStackRenderState stackRenderState = new ItemStackRenderState();
                final Minecraft mc = Minecraft.getInstance();

                mc.getItemModelResolver().updateForTopItem(stackRenderState, stack, displayContext, mc.level, null, (int)(long)renderState.getOrDefaultGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID, 0L) + displayContext.ordinal());
                stackRenderState.submit(poseStack, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, 0);

                poseStack.popPose();
            }
        });
        shadowRadius = 0.4f;
    }
}
