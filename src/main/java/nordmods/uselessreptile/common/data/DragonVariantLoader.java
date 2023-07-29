package nordmods.uselessreptile.common.data;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DragonVariantLoader {
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
                                JsonObject input = array.get(i).getAsJsonObject();
                                String name = input.get("name").getAsString();
                                int weight = input.get("weight").getAsInt();

                                DragonVariant.AllowedBiomes allowedBiomes = null;
                                if (input.has("allowed_biomes")) {
                                    JsonObject biomes = JsonHelper.getObject(input, "allowed_biomes");

                                    List<String> biomesById = new ArrayList<>();
                                    if (biomes.has("biome")) {
                                        JsonArray tags = biomes.get("biome").getAsJsonArray();
                                        for (int j = 0; j < tags.size(); j++) biomesById.add(tags.get(j).getAsString());
                                    }

                                    List<String> biomesByTag = new ArrayList<>();
                                    if (biomes.has("tag")) {
                                        JsonArray tags = biomes.get("tag").getAsJsonArray();
                                        for (int j = 0; j < tags.size(); j++) biomesByTag.add(tags.get(j).getAsString());
                                    }

                                    allowedBiomes = new DragonVariant.AllowedBiomes(biomesById, biomesByTag);
                                }

                                variants.add(new DragonVariant(name, weight, allowedBiomes));
                            }
                        } catch (JsonIOException e) {
                            UselessReptile.LOGGER.error("Failed to read json " + id, e);
                        }

                    } catch(Exception e) {
                        UselessReptile.LOGGER.error("Error occurred while loading resource json " + id, e);
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
