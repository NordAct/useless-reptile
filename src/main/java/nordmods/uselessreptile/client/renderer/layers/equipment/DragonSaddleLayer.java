package nordmods.uselessreptile.client.renderer.layers.equipment;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.common.entity.base.URRideableDragonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonSaddleLayer<T extends URRideableDragonEntity> extends URDragonEquipmentLayer<T>{
    public DragonSaddleLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn, EquipmentSlot.FEET);
    }
}
