package nordmods.uselessreptile.client.dragon_equipment;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
import nordmods.uselessreptile.client.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.client.asset_cache.EquipmentAssetCache;

import java.util.Collection;
import java.util.List;

public class DragonEquipment implements BRAnimatedObject, AssetCahceOwner {
    public LivingEntityRenderState ownerRenderState;
    public final ItemStack itemStack;
    private final EquipmentAssetCache assetCache;
    public final CloneAnimationController cloneController = new CloneAnimationController();
    public final EquipmentAnimationController controller;
    public final Collection<BRAnimationController> controllers;
    public final boolean isSaddle;

    public EquipmentAssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipment(ItemStack itemStack, EquipmentAssetCache assetCache, boolean isSaddle) {
        this.itemStack = itemStack;
        this.assetCache = assetCache;
        this.controller = new EquipmentAnimationController(assetCache.getAnimationLocationCache());
        this.controllers = List.of(cloneController, controller);
        this.isSaddle = isSaddle;
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }
}
