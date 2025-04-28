package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.Registries;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.model.DragonEqupmentModel;
import nordmods.uselessreptile.client.renderer.layers.URGlowingLayer;
import nordmods.uselessreptile.client.util.DragonEquipmentAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DragonEquipmentRenderer extends GeoObjectRenderer<DragonEquipmentAnimatable> {
    public DragonEquipmentRenderer() {
        super(new DragonEqupmentModel());
        addRenderLayer(new URGlowingLayer<>(this, state -> state.getGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE)));
    }

    @Override
    public void adjustPositionForRender(GeoRenderState renderState, MatrixStack poseStack, BakedGeoModel model, boolean isReRender) {
    }

    @Override
    public void addRenderData(DragonEquipmentAnimatable animatable, Void relatedObject, GeoRenderState renderState) {
        renderState.addGeckolibData(URDataTickets.DRAGON_RENDER_STATE, animatable.ownerRenderState);
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ITEM_ID, Registries.ITEM.getId(animatable.item));
        renderState.addGeckolibData(URDataTickets.EQUIPMENT_ASSET_CACHE, animatable.getAssetCache());
    }
}