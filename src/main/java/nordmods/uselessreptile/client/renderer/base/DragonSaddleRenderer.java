package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.client.init.URClientStateDataTypes;
import nordmods.uselessreptile.common.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.layers.BannerRenderLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.common.asset_cache.DragonAssetCache;
import nordmods.uselessreptile.common.entity.dragon_equipment.DragonEquipment;

public class DragonSaddleRenderer extends DragonEquipmentRenderer{
    public DragonSaddleRenderer() {
        addRenderLayer(new BannerRenderLayer(this));
        addRenderLayer(new DragonPassengerLayer(this));
    }

    @Override
    public void extractRenderState(DragonEquipment animatable, BRState.Impl state, float tickDelta) {
        super.extractRenderState(animatable, state, tickDelta);
        DragonEquipment banner = ((DragonAssetCache)animatable.ownerState.getStateData(URStateDataTypes.ASSET_CACHE)).getEquipment(EquipmentSlot.BODY);
        if (banner != null && banner.itemStack != null) {
            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(stackRenderState, banner.itemStack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 0);
            state.setStateData(URClientStateDataTypes.BANNER, stackRenderState);
        }
        state.setStateData(URClientStateDataTypes.PASSENGERS_RENDER_STATE, animatable.ownerState.getStateData(URClientStateDataTypes.PASSENGERS_RENDER_STATE));
        state.setStateData(URClientStateDataTypes.PASSENGERS_RENDERERS, animatable.ownerState.getStateData(URClientStateDataTypes.PASSENGERS_RENDERERS));
        state.setStateData(URClientStateDataTypes.PASSENGERS_UUID, animatable.ownerState.getStateData(URClientStateDataTypes.PASSENGERS_UUID));
        state.setStateData(URClientStateDataTypes.PASSENGERS_ATTACHMENT_POS, animatable.ownerState.getStateData(URClientStateDataTypes.PASSENGERS_ATTACHMENT_POS));
        state.setStateData(URClientStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, animatable.ownerState.getStateData(URClientStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT));
    }
}
