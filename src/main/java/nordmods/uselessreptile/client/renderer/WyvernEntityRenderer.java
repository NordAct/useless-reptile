package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.WyvernEntityModel;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonRenderer;
import nordmods.uselessreptile.common.entity.WyvernEntity;

public class WyvernEntityRenderer extends URRideableDragonRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernEntityModel(), true, false, "rider");
        shadowRadius = 1.5f;
    }

    public void updateSaddle (WyvernEntity entity) {
        boolean hasRider = entity.hasControllingPassenger();
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.SADDLE);
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
        model.getBone("saddle_neck1").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_neck3").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_neck5").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_front").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_back").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("ropes").ifPresent(c -> c.setHidden(!hasRider));
    }
}
