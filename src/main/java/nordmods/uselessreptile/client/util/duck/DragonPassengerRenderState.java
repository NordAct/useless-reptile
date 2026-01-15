package nordmods.uselessreptile.client.util.duck;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

//yes, that's a hack around whatever mojang wanted to do with entity rendering
public interface DragonPassengerRenderState {
    default boolean useless_reptile$isRidingDragon() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setRidingDragon(boolean state) {
        throw new AssertionError("Implemented in mixin");
    }
    @Nullable
    default UUID useless_reptile$getUUID() {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setUUID(UUID state) {
        throw new AssertionError("Implemented in mixin");
    }
}
