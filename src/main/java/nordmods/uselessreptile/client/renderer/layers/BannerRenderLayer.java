package nordmods.uselessreptile.client.renderer.layers;

import nordmods.uselessreptile.client.init.URDataTickets;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BannerRenderLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public BannerRenderLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, boolean didRenderModel, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        model.getBone("banner").ifPresent(bone ->
                consumer.accept(bone, (renderState1, poseStack, bone1, renderTasks, cameraState, packedLight, packedOverlay, renderColor) ->
                        renderForBone(renderState, bone, poseStack, renderTasks, packedLight)));
    }


    protected void renderForBone(R renderState, GeoBone bone, PoseStack matrixStackIn, SubmitNodeCollector renderTasks, int packedLight) {
            GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
            if (ownerState == null) return;
            ItemStack stack = ownerState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT).get(EquipmentSlot.OFFHAND);

            if (stack != null && !stack.isEmpty()) {
                RenderUtil.translateAndRotateMatrixForBone(matrixStackIn, bone);
                //bone.transformToBone(matrixStackIn);

                //matrixStackIn.scale(16f, 16f, 16f);
                ItemStackRenderState stackRenderState = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().updateForTopItem(stackRenderState, stack, ItemDisplayContext.NONE, ClientUtil.getLevel(), null, renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID).intValue());
                stackRenderState.submit(matrixStackIn, renderTasks, packedLight, OverlayTexture.NO_OVERLAY, 0);
            }
    }
}
