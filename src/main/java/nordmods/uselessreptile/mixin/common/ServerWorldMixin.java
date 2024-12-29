package nordmods.uselessreptile.mixin.common;

import net.minecraft.server.world.ServerWorld;
import nordmods.uselessreptile.common.util.duck.LightningChaserSpawnTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements LightningChaserSpawnTimer {
    @Unique
    private int spawnTimer = 600;

    @Override
    public int useless_reptile$getTimer() {
        return spawnTimer;
    }

    @Override
    public void useless_reptile$setTimer(int state) {
        spawnTimer = state;
    }
}
