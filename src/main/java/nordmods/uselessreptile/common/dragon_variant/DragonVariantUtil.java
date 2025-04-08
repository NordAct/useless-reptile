package nordmods.uselessreptile.common.dragon_variant;

import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import nordmods.uselessreptile.common.init.URRegistryKeys;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DragonVariantUtil {
    @Nullable
    public static DragonModel getDragonModelData(URDragonEntity dragon) {
        if (!ResourceUtil.isResourceReloadFinished) return null;
        return dragon.getWorld().getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_MODEL).get(DragonVariant.getDragonVariant(dragon).dragonModelData());
    }

    @Nullable
    public static DragonEquipment.Equipment getEquipmentModelData(URDragonEntity dragon, Item item) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        DragonVariant variant = DragonVariant.getDragonVariant(dragon);

        DynamicRegistryManager registryManager = dragon.getWorld().getRegistryManager();
        DragonEquipment dragonEquipment = registryManager.getOrThrow(URRegistryKeys.DRAGON_EQUIPMENT).get(variant.dragonEquipment());
        if (dragonEquipment == null) return null;

        List<DragonEquipment.Equipment> equipments = new ArrayList<>(dragonEquipment.equipment());
        equipments.addAll(getInjections(registryManager, variant.dragonEquipment()));

        Identifier parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            dragonEquipment = registryManager.getOrThrow(URRegistryKeys.DRAGON_EQUIPMENT).get(parent);
            if (dragonEquipment == null) break;
            equipments.addAll(dragonEquipment.equipment());
            equipments.addAll(getInjections(registryManager, parent));
            parent = dragonEquipment.parent().orElse(null);
        }

        Identifier id = Registries.ITEM.getId(item);
        for (DragonEquipment.Equipment equipment : equipments) if (equipment.item().equals(id)) return equipment;
        return null;
    }

    private static List<DragonEquipment.Equipment> getInjections(DynamicRegistryManager registryManager, Identifier parent) {
        List<DragonEquipment.Equipment> injections = new ArrayList<>();
        registryManager.getOrThrow(URRegistryKeys.DRAGON_EQUIPMENT_INJECT)
                .stream()
                .filter(inject -> inject.parent().isPresent() && inject.parent().get().equals(parent))
                .forEach(inject -> injections.addAll(inject.equipment()));
        return injections;
    }
}
