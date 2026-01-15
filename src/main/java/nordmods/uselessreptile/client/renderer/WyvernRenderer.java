package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import nordmods.uselessreptile.client.renderer.base.URRideableDragonEntityRenderer;
import nordmods.uselessreptile.common.entity.Wyvern;

public class WyvernRenderer extends URRideableDragonEntityRenderer<Wyvern> {
    public WyvernRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        shadowRadius = 1.5f;
    }
}
