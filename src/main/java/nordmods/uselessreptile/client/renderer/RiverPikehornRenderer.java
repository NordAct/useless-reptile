package nordmods.uselessreptile.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import nordmods.uselessreptile.client.init.URStateDataTypes;
import nordmods.uselessreptile.client.renderer.base.HeadMountDragonRenderer;
import nordmods.uselessreptile.client.renderer.layers.RiverPikehornFishRenderLayer;
import nordmods.uselessreptile.client.dragon_equipment.DragonEquipment;
import nordmods.uselessreptile.common.entity.RiverPikehorn;

public class RiverPikehornRenderer extends HeadMountDragonRenderer<RiverPikehorn> {
    public RiverPikehornRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        addRenderLayer(new RiverPikehornFishRenderLayer(this));
        shadowRadius = 0.4f;
    }

    @Override
    public void extractRenderState(RiverPikehorn animatable, LivingEntityRenderState state, float tickDelta) {
        super.extractRenderState(animatable, state, tickDelta);
        DragonEquipment equipment = animatable.getAssetCache().getEquipment(EquipmentSlot.OFFHAND);
        if (equipment != null && equipment.itemStack != null) {
            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(stackRenderState, equipment.itemStack, ItemDisplayContext.NONE, Minecraft.getInstance().level, animatable, animatable.getId());
            state.setStateData(URStateDataTypes.FISH, stackRenderState);
        }
     }
}
