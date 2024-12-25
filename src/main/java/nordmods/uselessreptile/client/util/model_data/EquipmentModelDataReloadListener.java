package nordmods.uselessreptile.client.util.model_data;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.model_data.base.EquipmentModelData;

import java.util.List;
import java.util.Map;

public class EquipmentModelDataReloadListener extends JsonDataLoader<List<EquipmentModelData>> implements IdentifiableResourceReloadListener {
    private static final ResourceFinder FINDER = ResourceFinder.json("dragon_model_data/equipment_model_data");
    public EquipmentModelDataReloadListener() {
        super(EquipmentModelData.CODEC.listOf(), FINDER);
    }

    @Override
    protected void apply(Map<Identifier, List<EquipmentModelData>> prepared, ResourceManager manager, Profiler profiler) {
        EquipmentModelData.reset();
        for (Map.Entry<Identifier, List<EquipmentModelData>> entry : prepared.entrySet()) {
            String path = entry.getKey().getPath();
            for (EquipmentModelData data : entry.getValue()) EquipmentModelData.add(path, data);
        }
        EquipmentModelData.debugPrint();
    }

    @Override
    public Identifier getFabricId() {
        return UselessReptile.id("dragon_model_data/equipment_model_data");
    }

    public static void init () {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new EquipmentModelDataReloadListener());
    }
}
