package nordmods.uselessreptile.common.util.duck;

public interface LightningChaserSpawnTimer {
    default int useless_reptile$getTimer()  {
        throw new AssertionError("Implemented in mixin");
    }
    default void useless_reptile$setTimer(int state)  {
        throw new AssertionError("Implemented in mixin");
    }
}
