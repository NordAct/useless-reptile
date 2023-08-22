package nordmods.uselessreptile.client.renderer.layers.armor;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

public class DragonTailArmorLayer<T extends URDragonEntity> extends URDragonArmorLayer<T> {
    public DragonTailArmorLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        slot = EquipmentSlot.LEGS;
    }
}
