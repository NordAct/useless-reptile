package nordmods.uselessreptile.client.util.model_data.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public record ModelData(@Nullable Identifier texture, @Nullable Identifier model, @Nullable Identifier animation) {
    public static ModelData deserialize(JsonElement element) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        Identifier texture = JsonHelper.hasString( object,"texture") ? new Identifier(JsonHelper.getString(object, "texture")) : null;
        Identifier model = JsonHelper.hasString( object,"model") ? new Identifier(JsonHelper.getString(object, "model")) : null;
        Identifier animation = JsonHelper.hasString( object,"animation") ? new Identifier(JsonHelper.getString(object, "animation")) : null;
        return new ModelData(texture, model, animation);
    }
}
