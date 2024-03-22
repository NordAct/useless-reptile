package nordmods.uselessreptile.client.util.model_data.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public record ModelData(Identifier texture, @Nullable Identifier model, @Nullable Identifier animation, RenderLayer renderType) {
    public static ModelData deserialize(JsonElement element) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        Identifier texture = new Identifier(JsonHelper.getString(object, "texture"));
        Identifier model = JsonHelper.hasString( object,"model") ? new Identifier(JsonHelper.getString(object, "model")) : null;
        Identifier animation = JsonHelper.hasString( object,"animation") ? new Identifier(JsonHelper.getString(object, "animation")) : null;

        boolean cull = JsonHelper.hasBoolean( object,"cull") ? JsonHelper.getBoolean(object, "cull") : true;
        boolean translucent = JsonHelper.hasBoolean( object,"translucent") ? JsonHelper.getBoolean(object, "translucent") : false;
        RenderLayer renderType;
        if (cull) renderType = translucent ? RenderLayer.getEntityTranslucentCull(texture) : RenderLayer.getEntityCutout(texture);
        else renderType = translucent ? RenderLayer.getEntityTranslucent(texture) : RenderLayer.getEntityCutoutNoCull(texture);

        return new ModelData(texture, model, animation, renderType);
    }
}
