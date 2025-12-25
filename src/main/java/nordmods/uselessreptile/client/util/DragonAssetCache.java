package nordmods.uselessreptile.client.util;

import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DragonAssetCache extends AssetCache {
    private HashMap<EquipmentSlot, DragonEquipment> equipmentMap = createEmptyEquipmentMap();

    @Override
    public void cleanCache() {
        super.cleanCache();
        equipmentMap.forEach((slot, animatable) -> {
            if (animatable != null) {
                animatable.getAssetCache().cleanCache();
            }
        });
        equipmentMap = createEmptyEquipmentMap();
    }

    public void setEquipment(EquipmentSlot slot, DragonEquipment equipmentAnimatable) {
        equipmentMap.put(slot, equipmentAnimatable);
    }

    @Nullable
    public DragonEquipment getEquipment(EquipmentSlot slot) {
        return equipmentMap.get(slot);
    }

    private static HashMap<EquipmentSlot, DragonEquipment> createEmptyEquipmentMap() {
        HashMap<EquipmentSlot, DragonEquipment> map = new HashMap<>(EquipmentSlot.values().length);
        for (EquipmentSlot slot : EquipmentSlot.values()) map.put(slot, null);
        return map;
    }
}