package nordmods.uselessreptile.client.util.model_data.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DragonModelData(ModelData modelData, @Nullable List<EquipmentModelData> equipmentModelDataOverrides, boolean nametagAccessible) {
    //dragon id, map<variant, dragon model data>
    public static final Map<String, Map<String, DragonModelData>> dragonModelDataHolder = new HashMap<>();

    public static DragonModelData deserialize(JsonElement element) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        ModelData modelData = ModelData.deserialize(JsonHelper.getElement(object, "model_data"));

        List<EquipmentModelData> overrides = null;
        if (JsonHelper.hasArray(object, "equipment_model_overrides")) {
            overrides = new ArrayList<>();
            for (JsonElement elem : JsonHelper.getArray(object, "equipment_model_overrides")) overrides.add(EquipmentModelData.deserialize(elem));
        }

        boolean nametagAccess = !JsonHelper.hasBoolean(object, "nametag_accessible") || JsonHelper.getBoolean(object, "nametag_accessible");

        return new DragonModelData(modelData, overrides, nametagAccess);
    }
}
