package nordmods.uselessreptile.client.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;

import java.util.ArrayList;
import java.util.List;

public class AssetCache {
    private Identifier modelLocationCache;
    private Identifier textureLocationCache;
    private Identifier animationLocationCache;
    private Identifier glowLayerLocationCache;
    private RenderLayer renderTypeCache;
    private boolean nametagModel;

    private List<EquipmentModelData> equipmentModelData;

    public Identifier getGlowLayerLocationCache() {
        return glowLayerLocationCache;
    }

    public void setGlowLayerLocationCache(Identifier state) {
        glowLayerLocationCache = state;
    }

    public Identifier getModelLocationCache() {
        return modelLocationCache;
    }

    public void setModelLocationCache(Identifier state) {
        modelLocationCache = state;
    }

    public Identifier getAnimationLocationCache() {
        return animationLocationCache;
    }

    public void setAnimationLocationCache(Identifier state) {
        animationLocationCache = state;
    }

    public Identifier getTextureLocationCache() {
        return textureLocationCache;
    }

    public void setTextureLocationCache(Identifier state) {
        textureLocationCache = state;
    }

    public RenderLayer getRenderTypeCache() {
        return renderTypeCache;
    }

    public void setRenderTypeCache(RenderLayer state) {
        renderTypeCache = state;
    }

    public boolean isNametagModel() {
        return nametagModel;
    }

    public void setNametagModel(boolean state) {
        nametagModel = state;
    }

    public List<EquipmentModelData> getEquipmentModelData() {
        return equipmentModelData;
    }

    public void setEquipmentModelData(List<EquipmentModelData> state) {
        equipmentModelData = state;
    }

    public void addEquipmentModelData(EquipmentModelData data) {
        if (equipmentModelData == null) {
            equipmentModelData = new ArrayList<>();
            equipmentModelData.add(data);
            return;
        }
        if (!equipmentModelData.contains(data)) equipmentModelData.add(data);
    }

    public EquipmentModelData getEquipmentModelData(Identifier item) {
        if (equipmentModelData == null) return null;
        return equipmentModelData.stream().filter(c -> c.item().equals(item)).findAny().orElse(null);
    }

    public EquipmentModelData getEquipmentModelData(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return getEquipmentModelData(id);
    }

    public void cleanCache() {
        modelLocationCache = null;
        textureLocationCache = null;
        animationLocationCache = null;
        glowLayerLocationCache = null;
        equipmentModelData = null;
        renderTypeCache = null;
        nametagModel = false;
    }
}
