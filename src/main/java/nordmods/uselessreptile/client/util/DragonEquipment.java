package nordmods.uselessreptile.client.util;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.animation.CloneAnimationController;

import java.util.Collection;
import java.util.List;

public class DragonEquipment implements BRAnimatedObject, AssetCahceOwner {
    public LivingEntityRenderState ownerRenderState;
    public final ItemStack itemStack;
    private final AssetCache assetCache = new EquipmentAssetCache();
    public final CloneAnimationController cloneController = new CloneAnimationController(true);
    public final Collection<BRAnimationController> controllers = List.of(cloneController); //todo equipment animations!!!

    public AssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipment(LivingEntityRenderState ownerRenderState, ItemStack itemStack) {
        this.ownerRenderState = ownerRenderState;
        this.itemStack = itemStack;
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }
}
