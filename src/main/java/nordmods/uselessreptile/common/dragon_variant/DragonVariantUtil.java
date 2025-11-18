package nordmods.uselessreptile.common.dragon_variant;

import nordmods.uselessreptile.client.util.ResourceUtil;
import nordmods.uselessreptile.common.dragon_variant.model.DragonEquipment;
import nordmods.uselessreptile.common.dragon_variant.model.DragonModel;
import nordmods.uselessreptile.common.init.URResourceKeys;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DragonVariantUtil {
    @Nullable
    public static DragonModel getDragonModelData(ResourceLocation dragonId, String name, String variant, Level world) {
        if (!ResourceUtil.isResourceReloadFinished) return null;
        return world.registryAccess().lookupOrThrow(URResourceKeys.DRAGON_MODEL).getValue(DragonVariant.getDragonVariant(dragonId, name, variant, world).dragonModelData());
    }

    @Nullable
    public static DragonEquipment.Equipment getEquipmentModelData(ResourceLocation dragonId, String name, String variant, Level world, ResourceLocation item) {
        if (!ResourceUtil.isResourceReloadFinished) return null;

        DragonVariant dragonVariant = DragonVariant.getDragonVariant(dragonId, name, variant, world);

        RegistryAccess registryManager = world.registryAccess();
        DragonEquipment dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(dragonVariant.dragonEquipment());
        if (dragonEquipment == null) return null;

        List<DragonEquipment.Equipment> equipments = new ArrayList<>(dragonEquipment.equipment());
        equipments.addAll(getInjections(registryManager, dragonVariant.dragonEquipment()));

        ResourceLocation parent = dragonEquipment.parent().orElse(null);
        while (parent != null) {
            dragonEquipment = registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT).getValue(parent);
            if (dragonEquipment == null) break;
            equipments.addAll(dragonEquipment.equipment());
            equipments.addAll(getInjections(registryManager, parent));
            parent = dragonEquipment.parent().orElse(null);
        }

        for (DragonEquipment.Equipment equipment : equipments) if (equipment.item().equals(item)) return equipment;
        return null;
    }

    private static List<DragonEquipment.Equipment> getInjections(RegistryAccess registryManager, ResourceLocation parent) {
        List<DragonEquipment.Equipment> injections = new ArrayList<>();
        registryManager.lookupOrThrow(URResourceKeys.DRAGON_EQUIPMENT_INJECT)
                .stream()
                .filter(inject -> inject.parent().isPresent() && inject.parent().get().equals(parent))
                .forEach(inject -> injections.addAll(inject.equipment()));
        return injections;
    }
}
