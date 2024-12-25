package nordmods.uselessreptile.common.util.dragon_spawn;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import nordmods.uselessreptile.UselessReptile;

import java.util.Map;

public class DragonSpawnReloadListener extends JsonDataLoader<DragonSpawn> implements IdentifiableResourceReloadListener {
    private static final ResourceFinder FINDER = ResourceFinder.json("dragon_spawns");
    public DragonSpawnReloadListener() {
        super(DragonSpawn.CODEC, FINDER);
    }

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DragonSpawnReloadListener());
    }

    @Override
    public Identifier getFabricId() {
        return UselessReptile.id("dragon_spawns");
    }

    @Override
    protected void apply(Map<Identifier, DragonSpawn> prepared, ResourceManager manager, Profiler profiler) {
        DragonSpawn.clearSpawns();
        for (Map.Entry<Identifier, DragonSpawn> entry : prepared.entrySet()) {
            String path = entry.getKey().getPath();
            String dragon = path.substring(0, path.indexOf("/"));
            DragonSpawn.addSpawn(dragon, entry.getValue());
        }
        DragonSpawn.debugPrint();
    }
}
