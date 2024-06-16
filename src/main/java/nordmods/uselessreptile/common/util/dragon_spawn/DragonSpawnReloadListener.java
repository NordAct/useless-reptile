package nordmods.uselessreptile.common.util.dragon_spawn;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nordmods.uselessreptile.UselessReptile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DragonSpawnReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public DragonSpawnReloadListener() {
        super(new GsonBuilder().create(), "dragon_spawns");
    }

    public static void init() {
        //todo
        //ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DragonSpawnReloadListener());
    }

    @Override
    public Identifier getFabricId() {
        return UselessReptile.id("dragon_spawns");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        DragonSpawn.dragonSpawnsHolder.clear();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            String path = entry.getKey().getPath();

            String dragon = path.substring(0, path.indexOf("/"));
            JsonElement element = entry.getValue();
            DragonSpawn data = DragonSpawn.deserialize(element);
            add(dragon, data);
        }
        debugPrint();
    }

    private void add(String name, DragonSpawn data) {
        List<DragonSpawn> content = DragonSpawn.dragonSpawnsHolder.get(name);
        if (content == null) content = new ArrayList<>();
        content.add(data);
        DragonSpawn.dragonSpawnsHolder.put(name, content);
    }


    private void debugPrint() {
        for (Map.Entry<String, List<DragonSpawn>> entry : DragonSpawn.dragonSpawnsHolder.entrySet()) {
            for (DragonSpawn spawn : entry.getValue()) {
                UselessReptile.LOGGER.debug("{}: added spawn for variant {} with conditions {}", entry.getKey(), spawn.variant(), spawn.condition());
            }
        }
    }
}
