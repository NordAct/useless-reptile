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

    @Override
    public void updateSaddle(LightningChaserEntity entity) {
        boolean hasSaddle = entity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.SADDLE);
        model.getBone("saddle_front").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_neck1").ifPresent(c -> c.setHidden(!hasSaddle));
        model.getBone("saddle_tail1").ifPresent(c -> c.setHidden(!hasSaddle));
    }
}
