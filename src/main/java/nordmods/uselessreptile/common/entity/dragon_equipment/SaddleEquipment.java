package nordmods.uselessreptile.common.entity.dragon_equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.biscuit_roll.common.animation.BRAnimationProcessorOwner;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.entity.animation_processor.EquipmentAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class SaddleEquipment extends DragonEquipment implements BRAnimationProcessorOwner<SaddleEquipment> {
    private final AnimationProcessor<SaddleEquipment> processor;
    public SaddleEquipment(URDragonEntity owner, ItemStack itemStack, EquipmentAssetCache assetCache, EquipmentSlot equipmentSlot) {
        super(owner, itemStack, assetCache, equipmentSlot);
        this.processor = new EquipmentAnimationProcessor<>(this);
        ((EquipmentAnimationProcessor<SaddleEquipment>)processor).getAssetCache().setModelLocationCache(assetCache.getModelLocationCache());
        ((EquipmentAnimationProcessor<SaddleEquipment>)processor).getAssetCache().setAnimationLocationCache(assetCache.getAnimationLocationCache());
    }

    public void tick() {
        super.tick();
        if (ownerHasAnimationProcessor()) {
            processor.tick();
        }
    }

    public AnimationProcessor<SaddleEquipment> getAnimationProcessor() {
        return processor;
    }

    @Override
    public boolean isClient() {
        return owner instanceof BRAnimationProcessorOwner<?> o && o.isClient();
    }

    @Override
    public void updateControllerStates() {} //owner stores the data

    @Override
    public float getAnimationTime() {
        return owner instanceof BRAnimationProcessorOwner<?> o ? o.getAnimationTime() : 0;
    }

    @Override
    public void updateAnimations() {
        if (!owner.level().isClientSide()) super.updateAnimations();
    }

    @Override
    public boolean tickAnimationProcessorOnClient() {
        return true;
    }
}
