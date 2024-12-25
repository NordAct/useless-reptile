package nordmods.uselessreptile.client.init;

import net.minecraft.client.render.item.property.numeric.NumericProperties;
import nordmods.uselessreptile.UselessReptile;
import nordmods.uselessreptile.client.item_property.FluteModeProperty;

public class URItemProperties {
    public static void init() {
        NumericProperties.ID_MAPPER.put(UselessReptile.id("flute_mode"), FluteModeProperty.CODEC);
    }
}
