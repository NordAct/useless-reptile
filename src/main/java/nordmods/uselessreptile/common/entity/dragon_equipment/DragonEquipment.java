package nordmods.uselessreptile.common.entity.dragon_equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.common.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.entity.animation_processor.EquipmentAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DragonEquipment implements BRAnimatedObject, AssetCahceOwner {
    public final URDragonEntity owner;
    public BRState ownerState;
    public final ItemStack itemStack;
    private final EquipmentAssetCache assetCache;
    public final EquipmentAnimationController controller;
    public final List<BRAnimationController> controllers;
    @Nullable
    private final EquipmentAnimationProcessor processor;
    public final EquipmentSlot equipmentSlot;

    public EquipmentAssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipment(URDragonEntity owner, ItemStack itemStack, EquipmentAssetCache assetCache, EquipmentSlot equipmentSlot) {
        this.owner = owner;
        this.itemStack = itemStack;
        this.assetCache = assetCache;
        this.controller = new EquipmentAnimationController(assetCache.getAnimationLocationCache());
        this.equipmentSlot = equipmentSlot;
        this.controllers = List.of(controller);
        this.processor = createServerAnimationProcessor();
    }

    @Override
    public List<BRAnimationController> getAnimationControllers() {
        return processor != null && processor.isClientSide() ? List.of() : controllers;
    }

    public void tick() {
        if (processor != null) processor.tick();
    }

    public EquipmentAnimationProcessor createServerAnimationProcessor() {
        return null;
    }

    @Nullable
    public EquipmentAnimationProcessor getAnimationProcessor() {
        return processor;
    }
}
