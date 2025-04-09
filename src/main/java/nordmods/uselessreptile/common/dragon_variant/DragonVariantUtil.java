package nordmods.uselessreptile.common.dragon_variant;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.init.URRegistryKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DragonVariantUtil {
    @Nullable
    public static DragonModel getDragonModelData(Identifier dragonId, String name, String variant, World world) {
        if (!ResourceUtil.isResourceReloadFinished) return null;
        return world.getRegistryManager().getOrThrow(URRegistryKeys.DRAGON_MODEL).get(DragonVariant.getDragonVariant(dragonId, name, variant, world).dragonModelData());
    }

    @Nullable
    public static DragonEquipment.Equipment getEquipmentModelData(Identifier dragonId, String name, String variant, World world, Identifier item) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        DragonVariant dragonVariant = DragonVariant.getDragonVariant(dragonId, name, variant, world);

        DynamicRegistryManager registryManager = world.getRegistryManager();
        DragonEquipment dragonEquipment = registryManager.getOrThrow(URRegistryKeys.DRAGON_EQUIPMENT).get(dragonVariant.dragonEquipment());
        if (dragonEquipment == null) return null;

        List<DragonEquipment.Equipment> equipments = new ArrayList<>(dragonEquipment.equipment());
        equipments.addAll(getInjections(registryManager, dragonVariant.dragonEquipment()));

        Identifier parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            dragonEquipment = registryManager.getOrThrow(URRegistryKeys.DRAGON_EQUIPMENT).get(parent);
            if (dragonEquipment == null) break;
            equipments.addAll(dragonEquipment.equipment());
            equipments.addAll(getInjections(registryManager, parent));
            parent = dragonEquipment.parent().orElse(null);
        }

        for (DragonEquipment.Equipment equipment : equipments) if (equipment.item().equals(item)) return equipment;
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
