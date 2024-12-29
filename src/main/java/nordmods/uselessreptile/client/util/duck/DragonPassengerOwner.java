package nordmods.uselessreptile.client.util.duck;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

//yes, that's a hack around whatever mojang wanted to do with entity rendering
public interface DragonPassengerOwner {
    boolean isRidingDragon();
    void setRidingDragon(boolean state);
    @Nullable
    UUID getUUID();
    void setUUID(UUID state);
}
