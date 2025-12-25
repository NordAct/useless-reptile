package nordmods.uselessreptile.client.renderer.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.layers.BannerRenderLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import nordmods.uselessreptile.client.util.DragonAssetCache;
import nordmods.uselessreptile.client.util.DragonEquipment;

public class DragonSaddleRenderer extends DragonEquipmentRenderer{
    public DragonSaddleRenderer() {
        addRenderLayer(new BannerRenderLayer(this));
        addRenderLayer(new DragonPassengerLayer(this));
    }

    @Override
    public void extractRenderState(DragonEquipment animatable, BRState.Impl state, float tickDelta) {
        super.extractRenderState(animatable, state, tickDelta);
        DragonEquipment banner = ((DragonAssetCache)animatable.ownerRenderState.getStateData(URStateDataTypes.ASSET_CACHE)).getEquipment(EquipmentSlot.OFFHAND);
        if (banner != null && banner.itemStack != null) {
            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(stackRenderState, banner.itemStack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 0);
            state.setStateData(URStateDataTypes.OFFHAND, stackRenderState);
        }
        state.setStateData(URStateDataTypes.PASSENGERS_RENDER_STATE, animatable.ownerRenderState.getStateData(URStateDataTypes.PASSENGERS_RENDER_STATE));
        state.setStateData(URStateDataTypes.PASSENGERS_RENDERERS, animatable.ownerRenderState.getStateData(URStateDataTypes.PASSENGERS_RENDERERS));
        state.setStateData(URStateDataTypes.PASSENGERS_UUID, animatable.ownerRenderState.getStateData(URStateDataTypes.PASSENGERS_UUID));
        state.setStateData(URStateDataTypes.PASSENGERS_ATTACHMENT_POS, animatable.ownerRenderState.getStateData(URStateDataTypes.PASSENGERS_ATTACHMENT_POS));
        state.setStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT, animatable.ownerRenderState.getStateData(URStateDataTypes.PASSENGERS_SHOULD_RENDER_TO_CLIENT));
    }
}
