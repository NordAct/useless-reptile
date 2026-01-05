package nordmods.uselessreptile.client.util;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;

import java.util.Collection;
import java.util.List;

public class DragonEquipment implements BRAnimatedObject, AssetCahceOwner {
    public LivingEntityRenderState ownerRenderState;
    public final ItemStack itemStack;
    private final AssetCache assetCache = new EquipmentAssetCache();
    public final CloneAnimationController cloneController = new CloneAnimationController(true);
    public final EquipmentAnimationController controller;
    public final Collection<BRAnimationController> controllers;

    public AssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipment(ItemStack itemStack, Identifier animationFile) {
        this.itemStack = itemStack;
        this.controller = new EquipmentAnimationController(animationFile);
        this.controllers = List.of(cloneController, controller);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }
}
