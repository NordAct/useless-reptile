package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.LightningChaserEntityModel;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonRenderer;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;

public class LightningChaserEntityRenderer extends URRideableDragonRenderer<LightningChaserEntity> {
    public LightningChaserEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new LightningChaserEntityModel(), true, true, "rider");
        shadowRadius = 1.5f;
    }
}
