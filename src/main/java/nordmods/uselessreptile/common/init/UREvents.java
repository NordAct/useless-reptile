package nordmods.uselessreptile.common.init;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import nordmods.uselessreptile.common.entity.multipart.MultipartDragon;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import nordmods.uselessreptile.common.entity.multipart.WorldMultipartHelper;
import nordmods.uselessreptile.common.mixin.common.WorldMixin;

public class UREvents {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon) {
                Int2ObjectMap<URDragonPart> partMap =  ((WorldMultipartHelper)world).getPartMap();
                for (URDragonPart part : dragon.getParts()) partMap.put(part.getId(), part);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon) {
                Int2ObjectMap<URDragonPart> partMap =  ((WorldMultipartHelper)world).getPartMap();
                for (URDragonPart part : dragon.getParts()) partMap.remove(part.getId());
            }
        });
    }
}
