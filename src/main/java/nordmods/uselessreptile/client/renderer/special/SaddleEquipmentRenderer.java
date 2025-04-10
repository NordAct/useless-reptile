package nordmods.uselessreptile.client.renderer.special;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.client.init.URDataTickets;
import nordmods.uselessreptile.client.renderer.base.DragonEquipmentRenderer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class SaddleEquipmentRenderer extends DragonEquipmentRenderer {
    public SaddleEquipmentRenderer() {
        super();
        addRenderLayer(new DragonPassengerLayer<>(this));
        addRenderLayer(new BlockAndItemGeoLayer<>(this,
                (bone ,state) -> {
                    if (!bone.getName().equals("banner")) return null;
                    GeoRenderState ownerState = state.getGeckolibData(URDataTickets.DRAGON_RENDER_STATE);
                    if (ownerState != null) return ownerState.getGeckolibData(URDataTickets.DRAGON_EQIPMENT).get(EquipmentSlot.OFFHAND);
                    return null;
                },
                (bone, state) -> null));
    }
}
