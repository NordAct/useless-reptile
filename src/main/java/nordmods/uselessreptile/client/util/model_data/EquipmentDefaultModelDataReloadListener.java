package nordmods.uselessreptile.client.util.model_data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentDefaultModelDataReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    //dragon id, list<equipment model data>
    public static final Map<String, List<EquipmentModelData>> equipmentDefaultModelDataHolder = new HashMap<>();
    public EquipmentDefaultModelDataReloadListener() {
        super(new GsonBuilder().create(), "equipment_default_model_data");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        equipmentDefaultModelDataHolder.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            for (JsonElement elem : array) add(entry.getKey().getPath(), EquipmentModelData.deserialize(elem));
        }
        debugPrint();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(UselessReptile.MODID, "equipment_default_model_data");
    }

    private void add(String dragon, EquipmentModelData equipmentModelData) {
        List<EquipmentModelData> content = equipmentDefaultModelDataHolder.get(dragon);
        if (content != null) {
            if (content.stream().noneMatch(c -> c.item() == equipmentModelData.item())) content.add(equipmentModelData);
        } else {
            content = new ArrayList<>();
            content.add(equipmentModelData);
            equipmentDefaultModelDataHolder.put(dragon, content);
        }
    }

    public void debugPrint() {
        for (Map.Entry<String, List<EquipmentModelData>> entry : equipmentDefaultModelDataHolder.entrySet()) {
            for (EquipmentModelData data : entry.getValue()) {
                UselessReptile.LOGGER.error("{}: {}", entry.getKey(), data);
            }
        }
    }

    public static void init () {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EquipmentDefaultModelDataReloadListener());
    }
}
