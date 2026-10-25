package nordmods.uselessreptile.common.entity.dragon_equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationProcessorOwner;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.uselessreptile.common.asset_cache.AssetCahceOwner;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DragonEquipment implements BRAnimatedObject, AssetCahceOwner {
    public final URDragonEntity owner;
    public BRState ownerState;
    public final ItemStack itemStack;
    private final EquipmentAssetCache assetCache;
    protected final EquipmentAnimationController equipmentAnimationController;
    public final CloneAnimationController cloneAnimationController = new CloneAnimationController();
    private final LinkedList<BRAnimationController> controllers;
    @Nullable

    public final EquipmentSlot equipmentSlot;
    private boolean firstAnimationUpdate = true;

    public EquipmentAssetCache getAssetCache() {
        return assetCache;
    }

    public DragonEquipment(URDragonEntity owner, ItemStack itemStack, EquipmentAssetCache assetCache, EquipmentSlot equipmentSlot) {
        this.owner = owner;
        this.itemStack = itemStack;
        this.assetCache = assetCache;
        this.equipmentAnimationController = new EquipmentAnimationController(this);
        this.equipmentSlot = equipmentSlot;
        this.controllers = new LinkedList<>(List.of(equipmentAnimationController, cloneAnimationController));
    }

    @Override
    public LinkedList<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    public void tick() {
        updateAnimations();
    }

    public void updateAnimations() {
        cloneAnimationController.copyFrom(owner.getAnimationControllers());

        Map<String, BRPlayingAnimation> shouldPlay = new HashMap<>();
        cloneAnimationController.getPlayingAnimations().forEach(animation -> {
            if (!animation.isFinished()) shouldPlay.put(animation.getAnimation().name(), animation);
        });

        shouldPlay.forEach((name, playingAnimation) -> {
            if (equipmentAnimationController.getAnimation(name) != null) return;

            equipmentAnimationController.playAnimation(name);
            BRPlayingAnimation animation = equipmentAnimationController.getAnimation(name);
            if (animation != null) {
                animation.setAnimationTime(playingAnimation.getActualAnimationTime());
            }
        });
        equipmentAnimationController.getPlayingAnimations().forEach(animation -> {
            if (!shouldPlay.containsKey(animation.getAnimation().name())) animation.stop();
        });
        markAnimationUpdated();
    }

    public boolean isFirstAnimationUpdate() {
        return firstAnimationUpdate;
    }

    public void markAnimationUpdated() {
        firstAnimationUpdate = false;
    }

    public boolean ownerHasAnimationProcessor() {
        return owner instanceof BRAnimationProcessorOwner<?>;
    }
}
