package nordmods.uselessreptile.client.util.model_data.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nordmods.uselessreptile.UselessReptile;

import java.util.*;

public record DragonModelData(ModelData modelData, Optional<List<EquipmentModelData>> equipmentModelDataOverrides, boolean nametagAccessible) {
    //dragon id, map<variant, dragon model data>
    private static final Map<String, Map<String, DragonModelData>> dragonModelDataHolder = new HashMap<>();

    public static final Codec<DragonModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ModelData.CODEC.fieldOf("model_data").forGetter(DragonModelData::modelData),
                    EquipmentModelData.CODEC.listOf().optionalFieldOf("equipment_model_overrides").forGetter(DragonModelData::equipmentModelDataOverrides),
                    Codec.BOOL.optionalFieldOf("nametag_accessible", true).forGetter(DragonModelData::nametagAccessible))
            .apply(instance, DragonModelData::new));

    public static void add(String dragon, String variant, DragonModelData modelData) {
        Map<String, DragonModelData> content = dragonModelDataHolder.get(dragon);
        if (content != null) {
            if (!content.containsKey(variant)) content.put(variant, modelData);
        } else {
            content = new HashMap<>();
            content.put(variant, modelData);
            dragonModelDataHolder.put(dragon, content);
        }
    }

    public static void debugPrint() {
        for (Map.Entry<String, Map<String, DragonModelData>> entry : dragonModelDataHolder.entrySet()) {
            for ( Map.Entry<String, DragonModelData> data : entry.getValue().entrySet()) {
                UselessReptile.LOGGER.debug("{}: {}, {}", entry.getKey(), data.getKey(), data.getValue());
            }
        }
    }

    public static void reset() {
        dragonModelDataHolder.clear();
    }

    public static Set<Map.Entry<String, Map<String, DragonModelData>>> getEntries() {
        return dragonModelDataHolder.entrySet();
    }

    public static Map<String, DragonModelData> getModelData(String dragon) {
        return dragonModelDataHolder.get(dragon);
    }
}
