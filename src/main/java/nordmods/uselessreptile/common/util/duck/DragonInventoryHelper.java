package nordmods.uselessreptile.common.util.duck;

import nordmods.uselessreptile.common.entity.base.URDragonEntity;

public interface DragonInventoryHelper {
    default void uselessreptile$openDragonInventoryScreen(URDragonEntity dragon) {
        throw new AssertionError("Implemented in mixin");
    }
}
