package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import nordmods.uselessreptile.client.model.LightningChaserEntityModel;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;
import nordmods.uselessreptile.common.entity.WyvernEntity;

public class LightningChaserEntityRenderer extends URRideableDragonRenderer<LightningChaserEntity>{
    public LightningChaserEntityRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new LightningChaserEntityModel(), true, true, true,
                0, 2.85f, -0.05f, 0, new String[]{"dragon"}, "rider");
        shadowRadius = 1.5f;
    }

    @Override
    public void updateSaddle(LightningChaserEntity entity) {

    }
}
