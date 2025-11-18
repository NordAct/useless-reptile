package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import nordmods.uselessreptile.common.entity.LightningChaserEntity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class LightningChaserBailOutGoal extends Goal {
    private final LightningChaserEntity entity;
    private BlockPos pointOfInterest;
    private int timeout = 0;

    public LightningChaserBailOutGoal(LightningChaserEntity entity) {
        this.entity = entity;
        setFlags(EnumSet.allOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        if (entity.isTame() || !entity.isChallenger()) return false;
        if (entity.getShouldBailOut()) return true;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (timeout > adjustedTickDelay(20*60)) return false;
        return this.canUse();
    }

    @Override
    public void start() {
        updatePointOfInterest();
        entity.setSurrendered(false);
        entity.setOrderedToSit(false);
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        if (canUse()) entity.discard();
    }

    @Override
    public void tick() {
        entity.getNavigation().moveTo(pointOfInterest.getX(), pointOfInterest.getY(), pointOfInterest.getZ(), 1);
        if (entity.distanceToSqr(new Vec3(pointOfInterest.getX(), entity.getY(), pointOfInterest.getZ())) < 16) updatePointOfInterest();
        timeout++;
    }

    private void updatePointOfInterest() {
        int dist = 512;
        BlockPos pos;
        do {
            pos = BlockPos.containing(entity.calculateViewVector(0, entity.getYRot()).scale(dist).add(entity.position()));
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            if (entity.level().getChunk(x, z, ChunkStatus.SURFACE, false) != null) break;
            dist -= 16;
        } while (true);
        pointOfInterest = new BlockPos(pos.getX(), 256, pos.getX());
    }
}
