package nordmods.uselessreptile.client.init;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import nordmods.uselessreptile.common.entity.multipart.MultipartDragon;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;
import nordmods.uselessreptile.common.entity.multipart.WorldMultipartHelper;
import nordmods.uselessreptile.common.mixin.common.WorldMixin;

public class URClientEvents {
    public static void init() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon) {
                Int2ObjectMap<URDragonPart> partMap =  ((WorldMultipartHelper)world).getPartMap();
                for (URDragonPart part : dragon.getParts()) partMap.put(part.getId(), part);
            }
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon) {
                Int2ObjectMap<URDragonPart> partMap =  ((WorldMultipartHelper)world).getPartMap();
                for (URDragonPart part : dragon.getParts()) partMap.remove(part.getId());
            }
        });
    }
}
