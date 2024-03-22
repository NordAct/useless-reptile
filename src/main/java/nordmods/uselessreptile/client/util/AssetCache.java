package nordmods.uselessreptile.client.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.util.model_data.base.DragonModelData;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AssetCache {
    private DragonModelData dragonModelData;
    @NotNull
    private final List<EquipmentModelData> equipmentModelData = new ArrayList<>();
    private Identifier glowLayerLocationCache;

    public Identifier getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }

    public void setGlowLayerLocationCache(Identifier state) {
        glowLayerLocationCache = state;
    }

    public DragonModelData getDragonModelData() {
        return dragonModelData;
    }

    public void setDragonModelData(DragonModelData state) {
        dragonModelData = state;
    }

    public @NotNull List<EquipmentModelData> getEquipmentModelData() {
        return equipmentModelData;
    }

    public void addEquipmentModelData(EquipmentModelData data) {
        if (!equipmentModelData.contains(data)) equipmentModelData.add(data);
    }

    public EquipmentModelData getEquipmentModelData(Identifier item) {
        return equipmentModelData.stream().filter(c -> c.item().equals(item)).findAny().orElse(null);
    }

    public EquipmentModelData getEquipmentModelData(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return getEquipmentModelData(id);
    }

    public void cleanCache() {
        setDragonModelData(null);
        getEquipmentModelData().clear();
        setGlowLayerLocationCache(null);
    }
}
