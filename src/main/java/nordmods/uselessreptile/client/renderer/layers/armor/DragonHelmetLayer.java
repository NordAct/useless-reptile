package nordmods.uselessreptile.client.renderer.layers.armor;

import net.minecraft.entity.EquipmentSlot;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;


public class DragonHelmetLayer<T extends URDragonEntity> extends URDragonArmorLayer<T> {
    public DragonHelmetLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        slot = EquipmentSlot.HEAD;
    }
}
