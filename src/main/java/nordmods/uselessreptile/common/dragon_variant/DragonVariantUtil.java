package nordmods.uselessreptile.common.dragon_variant;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.model.EquipmentModelData;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModelData;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DragonVariantUtil {
    @Nullable
    public static DragonModelData getDragonModelData(Identifier dragonId, String name, String variant, Level world) {
        if (!ResourceUtil.isResourceReloadFinished) return null;
        return world.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_MODEL).getValue(DragonVariant.getDragonVariant(dragonId, name, variant, world).dragonModelData());
    }

    @Nullable
    public static EquipmentModelData.Equipment getEquipmentModelData(Identifier dragonId, String name, String variant, Level world, Identifier item) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        DragonVariant dragonVariant = DragonVariant.getDragonVariant(dragonId, name, variant, world);

        RegistryAccess registryManager = world.registryAccess();
        EquipmentModelData dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(dragonVariant.dragonEquipment());
        if (dragonEquipment == null) return null;

        List<EquipmentModelData.Equipment> equipments = new ArrayList<>(dragonEquipment.equipment());
        equipments.addAll(getInjections(registryManager, dragonVariant.dragonEquipment()));

        Identifier parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(parent);
            if (dragonEquipment == null) break;
            equipments.addAll(dragonEquipment.equipment());
            equipments.addAll(getInjections(registryManager, parent));
            parent = dragonEquipment.parent().orElse(null);
        }

        for (EquipmentModelData.Equipment equipment : equipments) if (equipment.item().equals(item)) return equipment;
        return null;
    }

    private static List<EquipmentModelData.Equipment> getInjections(RegistryAccess registryManager, Identifier parent) {
        List<EquipmentModelData.Equipment> injections = new ArrayList<>();
        registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT_INJECT)
                .stream()
                .filter(inject -> inject.parent().isPresent() && inject.parent().get().equals(parent))
                .forEach(inject -> injections.addAll(inject.equipment()));
        return injections;
    }
}
