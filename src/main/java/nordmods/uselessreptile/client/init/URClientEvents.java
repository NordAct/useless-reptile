package nordmods.uselessreptile.client.init;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import nordmods.uselessreptile.common.entity.multipart.DragonPartsHolder;
import nordmods.uselessreptile.common.entity.multipart.MultipartDragon;
import nordmods.uselessreptile.common.entity.multipart.URDragonPart;

public class URClientEvents {
    public static void init() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon)
                for (URDragonPart part : dragon.getParts()) DragonPartsHolder.URDragonParts.put(part.getId(), part);
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof MultipartDragon dragon)
                for (URDragonPart part : dragon.getParts()) DragonPartsHolder.URDragonParts.remove(part.getId());
        });
    }
}
