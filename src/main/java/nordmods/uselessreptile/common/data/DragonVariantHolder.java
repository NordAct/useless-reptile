package nordmods.uselessreptile.common.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DragonVariantHolder {
    private static Map<String, List<DragonVariant>> dragonVariants = new HashMap<>();

    public static List<DragonVariant> getVariants(String name) {
        return dragonVariants.get(name);
    }

    public static void reset() {
        dragonVariants.clear();
    }

    public static void add(String name, List<DragonVariant> variants) {
        dragonVariants.put(name, variants);
    }
}
