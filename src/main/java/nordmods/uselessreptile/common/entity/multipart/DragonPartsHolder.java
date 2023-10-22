package nordmods.uselessreptile.common.entity.multipart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DragonPartsHolder {
    public static final Map<Integer, URDragonPart> URDragonParts = new HashMap<>();
    public static Collection<URDragonPart> getParts() {
        return URDragonParts.values();
    }
}
