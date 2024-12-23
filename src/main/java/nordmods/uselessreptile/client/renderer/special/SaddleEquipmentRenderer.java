package nordmods.uselessreptile.client.renderer.special;

import nordmods.uselessreptile.client.renderer.base.DragonEquipmentRenderer;
import nordmods.uselessreptile.client.renderer.layers.BannerLayer;
import nordmods.uselessreptile.client.renderer.layers.DragonPassengerLayer;

public class SaddleEquipmentRenderer extends DragonEquipmentRenderer {
    public SaddleEquipmentRenderer() {
        super();
        addRenderLayer(new DragonPassengerLayer<>(this, "rider"));
        addRenderLayer(new BannerLayer<>(this));
    }
}
