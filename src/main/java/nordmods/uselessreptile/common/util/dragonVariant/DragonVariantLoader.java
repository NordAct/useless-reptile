package nordmods.uselessreptile.common.util.dragonVariant;

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
                DragonVariantUtil.reset();
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
                            for (JsonElement elem : array) {
                                JsonObject input = elem.getAsJsonObject();
                                String name = input.get("name").getAsString();
                                int weight = input.get("weight").getAsInt();

                                DragonVariant.BiomeRestrictions allowedBiomes = getBiomes("allowed_biomes", input);
                                DragonVariant.BiomeRestrictions bannedBiomes = getBiomes("banned_biomes", input);
                                DragonVariant.AltitudeRestriction altitudeRestriction = getAltitude(input);

                                DragonVariant dragonVariant = new DragonVariant(name, weight, allowedBiomes, bannedBiomes, altitudeRestriction);
                                if (!variants.contains(dragonVariant)) variants.add(dragonVariant);
                            }
                        } catch (JsonIOException e) {
                            UselessReptile.LOGGER.error("Failed to read json " + id, e);
                        }

                    } catch(Exception e) {
                        UselessReptile.LOGGER.error("Error occurred while loading resource json " + id, e);
                    }

                    DragonVariantUtil.add(dragon, variants);
                }
                DragonVariantUtil.debugPrint();
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(UselessReptile.MODID, "dragon_variants");
            }
        });
    }

    private static DragonVariant.BiomeRestrictions getBiomes(String list, JsonObject input) {
        DragonVariant.BiomeRestrictions restrictions = null;
        if (input.has(list)) {
            JsonObject biomes = JsonHelper.getObject(input, list);

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

            restrictions = new DragonVariant.BiomeRestrictions(biomesById, biomesByTag);
        }
        return restrictions;
    }

    private static DragonVariant.AltitudeRestriction getAltitude(JsonObject input) {
        int min = -1000;
        int max = 1000;
        if (input.has("altitude")) {
            JsonObject object = JsonHelper.getObject(input, "altitude");
            if (object.has("min")) min = object.get("min").getAsInt();
            if (object.has("max")) max = object.get("max").getAsInt();
        }
        return new DragonVariant.AltitudeRestriction(min, max);
    }
}
