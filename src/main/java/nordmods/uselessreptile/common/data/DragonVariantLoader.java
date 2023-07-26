package nordmods.uselessreptile.common.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import nordmods.uselessreptile.UselessReptile;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//todo добавить условие спавна

public class DragonVariantLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public void reload(ResourceManager manager) {
                DragonVariantHolder.reset();
                Map<Identifier, Resource> resourceMap = manager.findResources("dragon_variants", path -> path.getPath().endsWith(".json"));
                for(Map.Entry<Identifier, Resource> entry: resourceMap.entrySet()) {
                    Identifier id = entry.getKey();
                    String path = id.getPath();
                    String dragon = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));

                    List<DragonVariant> variants = new ArrayList<>();

                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {

                        InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        try {
                            JsonElement element = JsonParser.parseReader(bufferedReader);
                            JsonArray array = JsonHelper.getArray((JsonObject) element, "variants");
                            for (int i = 0; i < array.size(); i++) {
                                String name = array.get(i).getAsJsonObject().get("name").getAsString();
                                int weight = array.get(i).getAsJsonObject().get("weight").getAsInt();
                                variants.add(new DragonVariant(name, weight));
                            }
                        } catch (JsonIOException e) {
                            LOGGER.error("Failed to read json " + id, e);
                        }

                    } catch(Exception e) {
                        LOGGER.error("Error occurred while loading resource json " + id, e);
                    }

                    DragonVariantHolder.add(dragon, variants);
                }
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(UselessReptile.MODID, "dragon_variants");
            }
        });
    }
}
