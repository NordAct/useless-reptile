package nordmods.uselessreptile.client.renderer.layers.armor;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonChestplateLayer<T extends URDragonEntity> extends URDragonArmorLayer<T> {
    public DragonChestplateLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        slot = EquipmentSlot.CHEST;
    }
}
