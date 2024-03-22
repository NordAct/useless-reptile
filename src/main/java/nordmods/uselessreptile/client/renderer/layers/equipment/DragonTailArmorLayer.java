package nordmods.uselessreptile.client.renderer.layers.equipment;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonTailArmorLayer<T extends URDragonEntity> extends URDragonEquipmentLayer<T> {
    public DragonTailArmorLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn, EquipmentSlot.LEGS);
    }
}