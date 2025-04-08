package nordmods.uselessreptile.client.util;

import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class DragonAssetCache extends AssetCache {
    private HashMap<EquipmentSlot, DragonEquipmentAnimatable> equipmentAnimatablesMap = createEmptyEquipmentMap();

    @Override
    public void cleanCache() {
        super.cleanCache();
        equipmentAnimatablesMap.forEach((slot, animatable) -> {
            if (animatable != null) {
                animatable.getAssetCache().cleanCache();
                animatable.equipmentBones.clear();
            }
        });
        equipmentAnimatablesMap = createEmptyEquipmentMap();
    }

    public void setEquipmentAnimatable(EquipmentSlot slot, DragonEquipmentAnimatable equipmentAnimatable) {
        equipmentAnimatablesMap.put(slot, equipmentAnimatable);}

    @Nullable
    public DragonEquipmentAnimatable getEquipmentAnimatable(EquipmentSlot slot) {
        return equipmentAnimatablesMap.get(slot);
    }

    private static HashMap<EquipmentSlot, DragonEquipmentAnimatable> createEmptyEquipmentMap() {
        HashMap<EquipmentSlot, DragonEquipmentAnimatable> map = new HashMap<>(EquipmentSlot.values().length);
        for (EquipmentSlot slot : EquipmentSlot.values()) map.put(slot, null);
        return map;
    }
}