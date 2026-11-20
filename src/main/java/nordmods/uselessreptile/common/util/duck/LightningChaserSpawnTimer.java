package nordmods.uselessreptile.common.util.duck;

public interface LightningChaserSpawnTimer {
    int SPAWN_COOLDOWN = 600;
    int DEFAULT_SPAWN_COOLDOWN = 24000;

    default int useless_reptile$getTimer()  {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setTimer(int state)  {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setTimer() {useless_reptile$setTimer(SPAWN_COOLDOWN);}
}
