package nordmods.uselessreptile.mixin.common;

import net.minecraft.server.world.ServerWorld;
import nordmods.uselessreptile.common.util.LightningChaserSpawnTimer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements LightningChaserSpawnTimer {
    @Unique
    private int spawnTimer = 600;

    @Override
    public int getTimer() {
        return spawnTimer;
    }

    @Override
    public void setTimer(int state) {
        spawnTimer = state;
    }
}
