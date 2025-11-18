package nordmods.uselessreptile.common.util.duck;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public interface HeadMountDragonOwner {
    default void useless_reptile$setHeadMountDragon(@NotNull CompoundTag nbtCompound)  {
        throw new AssertionError("Implemented in mixin");
    }
    @NotNull
    default CompoundTag useless_reptile$getHeadMountDragon()  {
        throw new AssertionError("Implemented in mixin");
    }
}
