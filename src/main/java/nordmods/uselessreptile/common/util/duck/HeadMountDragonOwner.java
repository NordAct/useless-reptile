package nordmods.uselessreptile.common.util.duck;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public interface HeadMountDragonOwner {
    void setHeadMountDragon(@NotNull NbtCompound nbtCompound);
    @NotNull NbtCompound getHeadMountDragon();
}
