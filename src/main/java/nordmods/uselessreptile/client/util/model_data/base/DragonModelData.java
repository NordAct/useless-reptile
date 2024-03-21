package nordmods.uselessreptile.client.util.model_data.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record DragonModelData(ModelData modelData, @Nullable List<EquipmentModelDataOverride> equipmentModelDataOverrides, boolean nametagAccessible) {
    public static DragonModelData deserialize(JsonElement element) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        ModelData modelData = ModelData.deserialize(JsonHelper.getElement(object, "model_data"));

        List<EquipmentModelDataOverride> overrides = null;
        if (JsonHelper.hasArray(object, "equipment_model_overrides")) {
            JsonArray array = JsonHelper.getArray(object, "equipment_model_overrides");
            overrides = new ArrayList<>();

            for (JsonElement elem : array) {
                JsonObject arrayObject = elem.getAsJsonObject();
                Identifier item = new Identifier(JsonHelper.getString(arrayObject, "item"));
                ModelData equipmentModelData = ModelData.deserialize(JsonHelper.getElement(arrayObject, "model_data"));
                overrides.add(new EquipmentModelDataOverride(item, equipmentModelData));
            }
        }

        boolean nametagAccess = !JsonHelper.hasBoolean(object, "nametag_accessible") || JsonHelper.getBoolean(object, "nametag_accessible");

        return new DragonModelData(modelData, overrides, nametagAccess);
    }
    private record EquipmentModelDataOverride(Identifier item, ModelData modelData) {}
}
