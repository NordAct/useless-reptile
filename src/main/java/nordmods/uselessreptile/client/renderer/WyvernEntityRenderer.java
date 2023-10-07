package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.WyvernEntityModel;
import nordmods.uselessreptile.common.entity.WyvernEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class WyvernEntityRenderer extends URRideableDragonRenderer<WyvernEntity> {
    public WyvernEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WyvernEntityModel(), false, true,
                0, 2.85f, -0.05f, 0, new String[]{"dragon"}, "front");
        shadowRadius = 1.5f;
    }

    public void updateSaddle (WyvernEntity entity) {
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelResource(entity));
        boolean hasRider = entity.hasPrimaryPassenger();
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("spikes_front").ifPresent(c -> c.setHidden(hasSaddle));
        model.getBone("ropes").ifPresent(c -> c.setHidden(!hasRider));
    }
}
