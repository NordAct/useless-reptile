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

    @Override
    public void extractRenderState(Wyvern animatable, LivingEntityRenderState renderState, float tickDelta) {
        super.extractRenderState(animatable, renderState, tickDelta);
        setBoneVisibility(renderState, "spikes_front", animatable.getItemBySlot(EquipmentSlot.SADDLE).isEmpty());
    }
}
