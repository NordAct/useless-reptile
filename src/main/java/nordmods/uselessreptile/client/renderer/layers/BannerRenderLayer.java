package nordmods.uselessreptile.client.renderer.layers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
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

public class BannerRenderLayer<T extends GeoAnimatable, R extends GeoRenderState> extends GeoRenderLayer<T, Void, R> {
    public BannerRenderLayer(GeoRenderer<T, Void, R> renderer) {
        super(renderer);
    }
    //todo wait for geckolib
    private final ItemRenderState itemRenderState = new ItemRenderState();

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        model.getBone("banner").ifPresent(bone -> renderForBone(renderState, bone, consumer));
    }


    protected void renderForBone(R renderState, GeoBone bone, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        consumer.accept(bone, (renderState2, matrixStackIn, bone2, renderType, bufferSource,
                               packedLight, packedOverlay, renderColor) -> {
            GeoRenderState ownerState = renderState.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
            if (ownerState == null) return;
            ItemStack stack = ownerState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT).get(EquipmentSlot.OFFHAND);

            if (stack != null && !stack.isEmpty()) {
                RenderUtil.translateAndRotateMatrixForBone(matrixStackIn, bone);
                ItemRenderer.renderItem(stack, ItemDisplayContext.NONE, packedLight, packedOverlay, matrixStackIn, bufferSource, ClientUtil.getLevel(),
                        renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID).intValue());
            }
        });
    }
}
