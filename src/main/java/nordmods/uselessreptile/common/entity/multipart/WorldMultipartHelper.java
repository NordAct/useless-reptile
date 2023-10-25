package nordmods.uselessreptile.common.entity.multipart;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Collection;

public interface WorldMultipartHelper {
    default Collection<URDragonPart> getParts() {
        return getPartMap().values();
    }

    Int2ObjectMap<URDragonPart> getPartMap();
}
