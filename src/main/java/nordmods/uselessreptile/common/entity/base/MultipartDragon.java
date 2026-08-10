package nordmods.uselessreptile.common.entity.base;

import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.uselessreptile.common.network.s2c.SyncEntityPartsPosPayload;

public interface MultipartDragon extends MultipartEntity {
    void sendSyncPayload();
    void handleSyncPayload(SyncEntityPartsPosPayload payload);
}
