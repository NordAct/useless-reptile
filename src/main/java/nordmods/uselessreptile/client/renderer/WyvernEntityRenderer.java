package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.WyvernEntityModel;
import nordmods.uselessreptile.common.entity.WyvernEntity;

public class WyvernEntityRenderer extends URRideableDragonRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernEntityModel(), true, true,
                0, 2.85f, -0.05f, 0, new String[]{"dragon"}, "front");
        shadowRadius = 1.5f;
    }

    public void updateSaddle (WyvernEntity entity) {
        boolean hasRider = entity.hasControllingPassenger();
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
        model.getBone("ropes").ifPresent(c -> c.setHidden(!hasRider));
    }
}
