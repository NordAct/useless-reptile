package nordmods.uselessreptile.common.entity.base;

import net.minecraft.util.math.Vec3d;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;
import nordmods.sap.ServerModelOwner;

public interface URMultipartEntity<T extends URDragonEntity> extends MultipartEntity, ServerModelOwner<T> {
    void updateNextPartPos(Vec3d[] relativePos);
    Vec3d[] getNextPartPos();
}
