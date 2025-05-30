package nordmods.uselessreptile.common.entity.base;

import net.minecraft.util.math.Vec3d;
import nordmods.primitive_multipart_entities.common.entity.MultipartEntity;

public interface URMultipartEntity extends MultipartEntity {
    void updatePartsPos(Vec3d[] relativePos);
}
