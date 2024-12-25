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
import nordmods.uselessreptile.client.util.model_data.base.DragonModelData;

import java.util.Map;

public class DragonModelDataReloadListener extends JsonDataLoader<DragonModelData> implements IdentifiableResourceReloadListener {
    private static final ResourceFinder FINDER = ResourceFinder.json("dragon_model_data");
    public DragonModelDataReloadListener() {
        super(DragonModelData.CODEC, FINDER);
    }

    @Override
    protected void apply(Map<Identifier, DragonModelData> prepared, ResourceManager manager, Profiler profiler) {
        DragonModelData.reset();
        for (Map.Entry<Identifier, DragonModelData> entry : prepared.entrySet()) {
            String path = entry.getKey().getPath();
            if (path.contains("equipment_model_data")) continue;

            String dragon = path.substring(0, path.indexOf("/"));
            String variant = path.substring(path.indexOf("/") + 1);
            DragonModelData.add(dragon, variant, entry.getValue());
        }
        DragonModelData.debugPrint();
    }

    @Override
    public Identifier getFabricId() {
        return UselessReptile.id("dragon_model_data");
    }

    public static void init () {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new DragonModelDataReloadListener());
    }
}
