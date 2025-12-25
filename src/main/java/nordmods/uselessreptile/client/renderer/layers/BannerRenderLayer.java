package nordmods.uselessreptile.client.renderer.layers;

import nordmods.biscuit_roll.client.renderer.layer.ItemRenderLayer;
import nordmods.biscuit_roll.common.state.BRState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.base.DragonEquipmentRenderer;
import org.jetbrains.annotations.Nullable;

public class BannerRenderLayer extends ItemRenderLayer {
    public BannerRenderLayer(DragonEquipmentRenderer parentRenderer) {
        super(parentRenderer);
    }

    @Override
    protected String getLocatorName() {
        return "banner";
    }

    @Override
    protected @Nullable ItemStackRenderState getItemStackRenderState(BRState state) {
        return state.getStateData(URStateDataTypes.OFFHAND);
    }
}
