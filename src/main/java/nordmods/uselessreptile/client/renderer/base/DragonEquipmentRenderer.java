package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.model.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable> {
    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        addRenderLayer(new URGlowingLayer<>(this, state -> state.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE)));
    }

    //have to override that because for some reason they give offset for matrix by 0.5 on each axis
    @Override
    public void preRender(GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
        objectRenderTranslations = new Matrix4f(poseStack.peek().getPositionMatrix());
        scaleModelForRender(renderState, scaleWidth, scaleHeight, poseStack, model, isReRender);
    }

    @Override
    public void addRenderData(DragonEquipmentAnimatable animatable, Void relatedObject, GeoRenderState renderState) {
        renderState.addGeckolibData(URDataTickets.DRAGON_RENDER_STATE, animatable.ownerRenderState);
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID, Registries.ITEM.getId(animatable.item));
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE, animatable.getAssetCache());
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, animatable.ownerRenderState.getGeckolibData(DataTickets.PACKED_LIGHT)); //todo maybe remove it later if Geckolib fixes it
    }
}