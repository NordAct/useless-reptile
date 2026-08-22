package nordmods.uselessreptile.common.entity.dragon_equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import nordmods.uselessreptile.common.asset_cache.EquipmentAssetCache;
import nordmods.uselessreptile.common.entity.animation_processor.EquipmentAnimationProcessor;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public class SaddleEquipment extends DragonEquipment {
    public SaddleEquipment(URDragonEntity owner, ItemStack itemStack, EquipmentAssetCache assetCache, EquipmentSlot equipmentSlot) {
        super(owner, itemStack, assetCache, equipmentSlot);
    }

    @Override
    public EquipmentAnimationProcessor createServerAnimationProcessor() {
        return owner.getAnimationProcessor() != null ? new EquipmentAnimationProcessor(this) : null;
    }
}
