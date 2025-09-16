package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;

import java.util.EnumSet;

public class LightningChaserBailOutGoal extends Goal {
    private final LightningChaserEntity entity;
    private BlockPos pointOfInterest;
    private int timeout = 0;

    public LightningChaserBailOutGoal(LightningChaserEntity entity) {
        this.entity = entity;
        setControls(EnumSet.allOf(Control.class));
    }

    @Override
    public boolean canStart() {
        if (entity.isTamed() || !entity.isChallenger()) return false;
        if (entity.getShouldBailOut()) return true;
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if (timeout > getTickCount(20*60)) return false;
        return this.canStart();
    }

    @Override
    public void start() {
        updatePointOfInterest();
        entity.setSurrendered(false);
        entity.setSitting(false);
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        if (canStart()) entity.discard();
    }

    @Override
    public void tick() {
        entity.getNavigation().startMovingTo(pointOfInterest.getX(), pointOfInterest.getY(), pointOfInterest.getZ(), 1);
        if (entity.squaredDistanceTo(new Vec3d(pointOfInterest.getX(), entity.getY(), pointOfInterest.getZ())) < 16) updatePointOfInterest();
        timeout++;
    }

    private void updatePointOfInterest() {
        int dist = 512;
        BlockPos pos;
        do {
            pos = BlockPos.ofFloored(entity.getRotationVector(0, entity.getYaw()).multiply(dist).add(entity.getPos()));
            int x = ChunkSectionPos.getSectionCoord(pos.getX());
            int z = ChunkSectionPos.getSectionCoord(pos.getZ());
            if (entity.getWorld().getChunk(x, z, ChunkStatus.SURFACE, false) != null) break;
            dist -= 16;
        } while (true);
        pointOfInterest = new BlockPos(pos.getX(), 256, pos.getX());
    }
}