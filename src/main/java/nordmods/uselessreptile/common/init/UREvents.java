package nordmods.uselessreptile.common.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import nordmods.uselessreptile.common.entity.multipart.DragonPartsHolder;
import nordmods.uselessreptile.common.entity.multipart.MultipartDragon;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;

public class UREvents {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon)
                for (URDragonPart part : dragon.getParts()) DragonPartsHolder.URDragonParts.put(part.getId(), part);
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon)
                for (URDragonPart part : dragon.getParts()) DragonPartsHolder.URDragonParts.remove(part.getId());
        });
    }
}
