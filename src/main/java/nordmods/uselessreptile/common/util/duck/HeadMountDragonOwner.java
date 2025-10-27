package nordmods.uselessreptile.common.util.duck;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public interface HeadMountDragonOwner {
    default void useless_reptile$setHeadMountDragon(@NotNull NbtCompound nbtCompound)  {
        throw new AssertionError("Implemented in mixin");
    }
    @NotNull
    default NbtCompound useless_reptile$getHeadMountDragon()  {
        throw new AssertionError("Implemented in mixin");
    }
}
