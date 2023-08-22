package nordmods.uselessreptile.common.entity.multipart;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Collection;

public class DragonPartsHolder {
    public static final Int2ObjectMap<URDragonPart> URDragonParts = new Int2ObjectOpenHashMap<>();
    public static Collection<URDragonPart> getParts() {
        return URDragonParts.values();
    }
}
