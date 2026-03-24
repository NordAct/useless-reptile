package nordmods.uselessreptile.common.dragon_variant;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.init.URResourceKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DragonVariantUtil {
    public static DragonModelData getDragonModelData(DragonVariant dragonVariant, HolderLookup.Provider provider) {
        return provider.lookupOrThrow(URResourceKeys.DRAGON_MODEL).getOrThrow(ResourceKey.create(URResourceKeys.DRAGON_MODEL, dragonVariant.common().dragonModelData())).value();
    }

    public static Map<Identifier, EquipmentModelData.Equipment> getEquipmentModelDataMap(DragonVariant dragonVariant, HolderLookup.Provider provider) {
        Optional<Holder.Reference<EquipmentModelData>> holder = provider
                .lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT)
                .get(ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, dragonVariant.common().dragonEquipment()));

        if (holder.isEmpty()) return new HashMap<>();

        EquipmentModelData dragonEquipment = holder.get().value();

        Map<Identifier, EquipmentModelData.Equipment> equipments = new HashMap<>(dragonEquipment.equipment());
        equipments.putAll(getInjections(provider, dragonVariant.common().dragonEquipment()));

        Identifier parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            holder = provider.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).get(ResourceKey.create(URResourceKeys.DRAGON_EQUIPMENT, parent));
            if (holder.isEmpty()) break;
            dragonEquipment = holder.get().value();
            dragonEquipment.equipment().forEach(equipments::putIfAbsent);
            getInjections(provider, parent).forEach(equipments::putIfAbsent);
            parent = dragonEquipment.parent().orElse(null);
        }

        return equipments;
    }

    private static Map<Identifier, EquipmentModelData.Equipment> getInjections(HolderLookup.Provider provider, Identifier parent) {
        Map<Identifier, EquipmentModelData.Equipment> injections = new HashMap<>();
        provider.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT_INJECT)
                .listElements()
                .map(Holder.Reference::value)
                .filter(inject -> inject.parent().isPresent() && inject.parent().get().equals(parent))
                .forEach(inject -> injections.putAll(inject.equipment()));
        return injections;
    }
}
