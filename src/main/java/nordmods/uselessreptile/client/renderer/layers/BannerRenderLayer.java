package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
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

import java.util.function.BiConsumer;

public class BannerRenderLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    public BannerRenderLayer(GeoRenderer<T, O, R> renderer) {
        super(renderer);
    }

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, boolean didRenderModel, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        model.getBone("banner").ifPresent(bone ->
                consumer.accept(bone, (renderState1, poseStack, bone1, renderTasks, cameraState, packedLight, packedOverlay, renderColor) ->
                        renderForBone(renderState, bone, consumer, renderTasks)));
    }


    protected void renderForBone(R renderState, GeoBone bone, BiConsumer<GeoBone, PerBoneRender<R>> consumer, OrderedRenderCommandQueue renderTasks) {
        consumer.accept(bone, (renderState2, matrixStackIn, bone2, renderType, bufferSource,
                               packedLight, packedOverlay, renderColor) -> {
            GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
            if (ownerState == null) return;
            ItemStack stack = ownerState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT).get(EquipmentSlot.OFFHAND);

            if (stack != null && !stack.isEmpty()) {
                RenderUtil.translateAndRotateMatrixForBone(matrixStackIn, bone);
                ItemRenderState stackRenderState = new ItemRenderState();
                MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(stackRenderState, stack, ItemDisplayContext.NONE, ClientUtil.getLevel(), null, renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID).intValue());
                stackRenderState.render(matrixStackIn, renderTasks, packedLight, OverlayTexture.DEFAULT_UV, 0);
            }
        });
    }
}
