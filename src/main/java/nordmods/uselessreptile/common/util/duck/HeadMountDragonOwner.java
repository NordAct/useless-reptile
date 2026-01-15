package nordmods.uselessreptile.common.util.duck;

import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.NonNull;

public interface HeadMountDragonOwner {
    default void useless_reptile$setHeadMountDragon(@NonNull CompoundTag nbtCompound)  {
        throw new AssertionError("Implemented in mixin");
    }
    @NonNull
    default CompoundTag useless_reptile$getHeadMountDragon()  {
        throw new AssertionError("Implemented in mixin");
    }
}
