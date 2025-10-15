package nordmods.uselessreptile.client.renderer.special;

import nordmods.uselessreptile.client.renderer.base.DragonEquipmentRenderer;
import nordmods.uselessreptile.client.renderer.layers.BannerRenderLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;

public class SaddleEquipmentRenderer extends DragonEquipmentRenderer {
    public SaddleEquipmentRenderer() {
        super();
        withRenderLayer(new DragonPassengerLayer<>(this));
        withRenderLayer(new BannerRenderLayer<>(this));
    }
}
