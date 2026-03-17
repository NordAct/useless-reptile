package nordmods.uselessreptile.common.dragon_variant;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.HashMap;
import java.util.Map;

public class DragonVariantUtil {
    public static DragonModelData getDragonModelData(DragonVariant dragonVariant, Level level) {
        return level.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_MODEL).getValue(dragonVariant.common().dragonModelData());
    }

    public static Map<Identifier, EquipmentModelData.Equipment> getEquipmentModelDataMap(DragonVariant dragonVariant, Level level) {
        RegistryAccess registryManager = level.registryAccess();
        EquipmentModelData dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(dragonVariant.common().dragonEquipment());
        if (dragonEquipment == null) return new HashMap<>();

        Map<Identifier, EquipmentModelData.Equipment> equipments = new HashMap<>(dragonEquipment.equipment());
        equipments.putAll(getInjections(registryManager, dragonVariant.common().dragonEquipment()));

        Identifier parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(parent);
            if (dragonEquipment == null) break;
            dragonEquipment.equipment().forEach(equipments::putIfAbsent);
            getInjections(registryManager, parent).forEach(equipments::putIfAbsent);
            parent = dragonEquipment.parent().orElse(null);
        }

        return equipments;
    }

    private static Map<Identifier, EquipmentModelData.Equipment> getInjections(RegistryAccess registryManager, Identifier parent) {
        Map<Identifier, EquipmentModelData.Equipment> injections = new HashMap<>();
        registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT_INJECT)
                .stream()
                .filter(inject -> inject.parent().isPresent() && inject.parent().get().equals(parent))
                .forEach(inject -> injections.putAll(inject.equipment()));
        return injections;
    }
}
