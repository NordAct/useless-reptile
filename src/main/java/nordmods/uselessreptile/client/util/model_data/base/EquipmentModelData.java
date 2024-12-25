package nordmods.uselessreptile.client.util.model_data.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import nordmods.uselessreptile.UselessReptile;

import java.util.*;

public record EquipmentModelData(Identifier item, ModelData modelData) {
    //dragon id, list<equipment model data>
    private static final Map<String, List<EquipmentModelData>> equipmentModelDataHolder = new HashMap<>();

    public static final Codec<EquipmentModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(EquipmentModelData::item),
            ModelData.CODEC.fieldOf("model_data").forGetter(EquipmentModelData::modelData))
            .apply(instance, EquipmentModelData::new));

    public static void add(String dragon, EquipmentModelData equipmentModelData) {
        List<EquipmentModelData> content = equipmentModelDataHolder.get(dragon);
        if (content != null) {
            if (content.stream().noneMatch(c -> c.item().equals(equipmentModelData.item()))) content.add(equipmentModelData);
        } else {
            content = new ArrayList<>();
            content.add(equipmentModelData);
            equipmentModelDataHolder.put(dragon, content);
        }
    }

    public static void debugPrint() {
        for (Map.Entry<String, List<EquipmentModelData>> entry : equipmentModelDataHolder.entrySet()) {
            for (EquipmentModelData data : entry.getValue()) {
                UselessReptile.LOGGER.debug("{}: {}", entry.getKey(), data);
            }
        }
    }

    public static void reset() {
        equipmentModelDataHolder.clear();
    }

    public static Set<Map.Entry<String, List<EquipmentModelData>>> getEntries() {
        return equipmentModelDataHolder.entrySet();
    }

    public static List<EquipmentModelData> getModelData(String dragon) {
        return equipmentModelDataHolder.get(dragon);
    }
}
