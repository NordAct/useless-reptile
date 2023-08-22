package nordmods.uselessreptile.common.entity.multipart;

import nordmods.uselessreptile.common.entity.multipart.URDragonPart;

public interface MultipartDragon {
    URDragonPart[] getParts();

    void updateChildParts();
}
