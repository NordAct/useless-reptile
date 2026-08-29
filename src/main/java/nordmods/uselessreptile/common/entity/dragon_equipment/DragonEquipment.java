package nordmods.uselessreptile.common.entity.dragon_equipment;

import libs.gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
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
    public final EquipmentAnimationController equipmentAnimationController;
    public final CloneAnimationController cloneAnimationController = new CloneAnimationController();
    private final List<BRAnimationController> controllers;
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
        this.equipmentAnimationController = new EquipmentAnimationController(assetCache.getAnimationLocationCache(), owner.level().isClientSide());
        this.equipmentSlot = equipmentSlot;
        this.controllers = List.of(equipmentAnimationController, cloneAnimationController);
        this.processor = createServerAnimationProcessor();
        if (processor != null) {
            processor.getAssetCache().setModelLocationCache(assetCache.getModelLocationCache());
            processor.getAssetCache().setAnimationLocationCache(assetCache.getAnimationLocationCache());
        }
    }

    @Override
    public List<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    public void tick() {
        if (owner.getAnimationProcessor() == null || !owner.level().isClientSide()){
            cloneAnimationController.copyFrom(owner.getAnimationControllers());
            owner.getAnimationControllers().forEach(controller -> {
                controller.getPlayingAnimations().forEach(playingAnimation -> {
                    if (playingAnimation.isDone()) return;
                    String name = playingAnimation.getAnimation().name();
                    if (equipmentAnimationController.getAnimation(name) != null) {
                        equipmentAnimationController.playAnimation(
                                name,
                                playingAnimation.getTransitionInTime(),
                                playingAnimation.getTransitionOutTime(),
                                playingAnimation.getTransitionInLerp(),
                                playingAnimation.getTransitionOutLerp()
                        );
                        return;
                    }
                    AnimationData data = equipmentAnimationController.getAnimationData(name);
                    if (data != null) {
                        BRPlayingAnimation animation = new BRPlayingAnimation(
                                data,
                                playingAnimation.getTransitionInTime(),
                                playingAnimation.getTransitionOutTime(),
                                playingAnimation.getTransitionInLerp(),
                                playingAnimation.getTransitionOutLerp(),
                                playingAnimation.getTransitionInTime() * playingAnimation.getTransitionInLerp().apply(playingAnimation.getTransitionInProgress())
                        );
                        animation.setAnimationTime(playingAnimation.getAnimationTime());
                        equipmentAnimationController.playAnimation(animation);
                    }
                });
                equipmentAnimationController.checkAgainstOtherController(controller);
            });
        }
        if (processor != null) {
            processor.tick();
        }
    }

    public EquipmentAnimationProcessor createServerAnimationProcessor() {
        return null;
    }

    @Nullable
    public EquipmentAnimationProcessor getAnimationProcessor() {
        return processor;
    }
}
