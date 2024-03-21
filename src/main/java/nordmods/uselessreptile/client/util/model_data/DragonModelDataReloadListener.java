package nordmods.uselessreptile.client.util.model_data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.util.model_data.base.DragonModelData;

import java.util.HashMap;
import java.util.Map;

public class DragonModelDataReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    //dragon id, map<variant, dragon model data>
    public static final Map<String, Map<String, DragonModelData>> dragonModelDataHolder = new HashMap<>();
    public DragonModelDataReloadListener() {
        super(new GsonBuilder().create(), "dragon_model_data");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        dragonModelDataHolder.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            String path = entry.getKey().getPath();
            String dragon = path.substring(0, path.indexOf("/"));
            String variant = path.substring(path.indexOf("/") + 1);
            JsonElement element = entry.getValue();
            DragonModelData data = DragonModelData.deserialize(element);
            add(dragon, variant, data);
        }
        debugPrint();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(UselessReptile.MODID, "dragon_model_data");
    }

    private void add(String dragon, String variant, DragonModelData modelData) {
        Map<String, DragonModelData> content = dragonModelDataHolder.get(dragon);
        if (content != null) {
            if (!content.containsKey(variant)) content.put(variant, modelData);
        } else {
            content = new HashMap<>();
            content.put(variant, modelData);
            dragonModelDataHolder.put(dragon, content);
        }
    }

    public void debugPrint() {
        for (Map.Entry<String, Map<String, DragonModelData>> entry : dragonModelDataHolder.entrySet()) {
            for ( Map.Entry<String, DragonModelData> data : entry.getValue().entrySet()) {
                UselessReptile.LOGGER.error("{}: {}, {}", entry.getKey(), data.getKey(), data.getValue());
            }
        }
    }

    public static void init () {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new DragonModelDataReloadListener());
    }
}
