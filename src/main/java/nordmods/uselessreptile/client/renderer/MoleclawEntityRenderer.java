package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.MoleclawEntityModel;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonRenderer;
import nordmods.uselessreptile.common.entity.MoleclawEntity;

public class MoleclawEntityRenderer extends URRideableDragonRenderer<MoleclawEntity> {
    public MoleclawEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MoleclawEntityModel(), true, true, "rider");
        shadowRadius = 1.25f;
    }

    public void updateSaddle (MoleclawEntity entity) {
        boolean isSaddled = entity.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.SADDLE;
        model.getBone("saddle_front").ifPresent(c -> c.setHidden(!isSaddled));
        model.getBone("saddle_neck").ifPresent(c -> c.setHidden(!isSaddled));
    }
}
