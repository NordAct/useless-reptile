package nordmods.uselessreptile.common.util.dragon_spawn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DragonSpawn(String variant, SpawnCondition condition) {
    public static final Map<String, List<DragonSpawn>> dragonSpawnsHolder = new HashMap<>();

    public static DragonSpawn deserialize(JsonElement element) throws JsonParseException {
        JsonObject input = element.getAsJsonObject();
        String name = input.get("variant").getAsString();
        SpawnCondition spawnCondition = SpawnCondition.deserialize(input.get("conditions"));
        return new DragonSpawn(name, spawnCondition);
    }

    public record SpawnCondition(int weight,
                                 @Nullable BiomeRestrictions allowedBiomes,
                                 @Nullable BiomeRestrictions bannedBiomes,
                                 @Nullable BlockRestrictions allowedBlocks,
                                 @Nullable BlockRestrictions bannedBlocks,
                                 AltitudeRestriction altitudeRestriction
                                 ) {
        //allowed - works as whitelist if presented
        //banned - works as blacklist if presented
        public boolean hasAllowedBiomes() {
            return allowedBiomes != null && (allowedBiomes.hasBiomesByIdList() || allowedBiomes.hasBiomesByTagList());
        }

        public boolean hasBannedBiomes() {
            return bannedBiomes != null && (bannedBiomes.hasBiomesByIdList() || bannedBiomes.hasBiomesByTagList());
        }

        public boolean hasAllowedBlocks() {
            return allowedBlocks != null && (allowedBlocks.hasBlocksByIdList() || allowedBlocks.hasBlocksByTagList());
        }

        public boolean hasBannedBlocks() {
            return bannedBlocks != null && (bannedBlocks.hasBlocksByIdList() || bannedBlocks.hasBlocksByTagList());
        }

        public record BiomeRestrictions(List<String> biomesById, List<String> biomesByTag) {
            public boolean hasBiomesByIdList() {
                return biomesById != null && !biomesById.isEmpty();
            }

            public boolean hasBiomesByTagList() {
                return biomesByTag != null && !biomesByTag.isEmpty();
            }
        }

        public record BlockRestrictions(List<String> blocksById, List<String> blocksByTag) {
            public boolean hasBlocksByIdList() {
                return blocksById != null && !blocksById.isEmpty();
            }

            public boolean hasBlocksByTagList() {
                return blocksByTag != null && !blocksByTag.isEmpty();
            }
        }

        private static SpawnCondition deserialize(JsonElement element) throws JsonParseException {
            JsonObject input = element.getAsJsonObject();
            int weight = input.get("weight").getAsInt();

            SpawnCondition.BiomeRestrictions allowedBiomes = getBiomes("allowed_biomes", input);
            SpawnCondition.BiomeRestrictions bannedBiomes = getBiomes("banned_biomes", input);
            SpawnCondition.BlockRestrictions allowedBlocks = getBlocks("allowed_blocks", input);
            SpawnCondition.BlockRestrictions bannedBlocks = getBlocks("banned_blocks", input);
            SpawnCondition.AltitudeRestriction altitudeRestriction = getAltitude(input);

            return new SpawnCondition(weight, allowedBiomes, bannedBiomes, allowedBlocks, bannedBlocks, altitudeRestriction);
        }

        private static SpawnCondition.BiomeRestrictions getBiomes(String list, JsonObject input) {
            SpawnCondition.BiomeRestrictions restrictions = null;
            if (input.has(list)) {
                JsonArray entries = JsonHelper.getArray(input, list);
                List<String> ids = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                entries.forEach(c -> {
                    String entry = c.getAsString();
                    if (entry.startsWith("#")) tags.add(entry.replaceFirst("#",""));
                    else ids.add(entry);
                });
                restrictions = new SpawnCondition.BiomeRestrictions(ids, tags);
            }
            return restrictions;
        }

        private static SpawnCondition.BlockRestrictions getBlocks(String list, JsonObject input) {
            SpawnCondition.BlockRestrictions restrictions = null;
            if (input.has(list)) {
                JsonArray entries = JsonHelper.getArray(input, list);
                List<String> ids = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                entries.forEach(c -> {
                    String entry = c.getAsString();
                    if (entry.startsWith("#")) tags.add(entry.replaceFirst("#",""));
                    else ids.add(entry);
                });
                restrictions = new SpawnCondition.BlockRestrictions(ids, tags);
            }
            return restrictions;
        }

        private static SpawnCondition.AltitudeRestriction getAltitude(JsonObject input) {
            int min = -1000;
            int max = 1000;
            if (input.has("altitude")) {
                JsonObject object = JsonHelper.getObject(input, "altitude");
                if (object.has("min")) min = object.get("min").getAsInt();
                if (object.has("max")) max = object.get("max").getAsInt();
            }
            return new SpawnCondition.AltitudeRestriction(min, max);
        }

        public record AltitudeRestriction(int min, int max) {}
    }
}
