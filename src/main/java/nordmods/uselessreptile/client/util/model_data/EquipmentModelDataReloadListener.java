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

import java.util.Map;

public class EquipmentModelDataReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public EquipmentModelDataReloadListener() {
        super(new GsonBuilder().create(), "dragon_equipment_model_data");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        EquipmentModelData.reset();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            for (JsonElement elem : array) EquipmentModelData.add(entry.getKey().getPath(), EquipmentModelData.deserialize(elem));
        }
        EquipmentModelData.debugPrint();
    }

    @Override
    public Identifier getFabricId() {
        return UselessReptile.id("dragon_equipment_model_data");
    }

    public static void init () {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EquipmentModelDataReloadListener());
    }
}
