package nordmods.uselessreptile.client.util.model_redirect;

import com.google.gson.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import nordmods.uselessreptile.UselessReptile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ModelRedirectLoader {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public void reload(ResourceManager manager) {
                ModelRedirectUtil.dragonModelRedirects.clear();
                Map<Identifier, Resource> resourceMap = manager.findResources("model_redirects", path -> path.getPath().endsWith(".json"));
                for(Map.Entry<Identifier, Resource> entry: resourceMap.entrySet()) {
                    Identifier id = entry.getKey();
                    String path = id.getPath();
                    String dragon = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".json"));

                    Map<String, ModelRedirect> map = new HashMap<>();

                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {

                        InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        try {
                            JsonElement element = JsonParser.parseReader(bufferedReader);
                            JsonArray array = JsonHelper.getArray((JsonObject) element, "redirects");
                            for (JsonElement elem : array) {
                                JsonObject input = elem.getAsJsonObject();
                                String name = input.get("name").getAsString();

                                String model = input.has("model") ? input.get("model").getAsString() : null;
                                String animation = input.has("animation") ? input.get("animation").getAsString() : null;
                                boolean nameTagAccessible = input.has("nametag_accessible") ? input.get("nametag_accessible").getAsBoolean() : true;

                                if (!map.containsKey(name)) map.put(name, new ModelRedirect(model, animation, nameTagAccessible));
                            }
                        } catch (JsonIOException e) {
                            UselessReptile.LOGGER.error("Failed to read json " + id, e);
                        }
                        ModelRedirectUtil.add(dragon, map);

                    } catch (Exception e) {
                        UselessReptile.LOGGER.error("Error occurred while loading resource json " + id, e);
                    }
                }
                ModelRedirectUtil.debugPrint();
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(UselessReptile.MODID, "model_redirects");
            }
        });
    }
}
