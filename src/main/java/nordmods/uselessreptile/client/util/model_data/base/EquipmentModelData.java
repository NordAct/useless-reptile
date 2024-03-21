package nordmods.uselessreptile.client.util.model_data.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public record EquipmentModelData (Identifier item, ModelData modelData) {
    public static EquipmentModelData deserialize(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        Identifier item = new Identifier(JsonHelper.getString(object, "item"));
        ModelData data = ModelData.deserialize(JsonHelper.getElement(object, "model_data"));

        return new EquipmentModelData(item, data);
    }
}
