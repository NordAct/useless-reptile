package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.LightningChaser;

import java.util.EnumSet;

public class LightningChaserBailOutGoal extends Goal {
    private final LightningChaser mob;
    private BlockPos pointOfInterest;
    private int timeout = 0;

    public LightningChaserBailOutGoal(LightningChaser mob) {
        this.mob = mob;
        setFlags(EnumSet.allOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        if (mob.isTame() || !mob.isChallenger()) return false;
        return mob.getShouldBailOut();
    }

    @Override
    public boolean canContinueToUse() {
        if (timeout > adjustedTickDelay(20*60)) return false;
        return this.canUse();
    }

    @Override
    public void start() {
        updatePointOfInterest();
        mob.setSurrendered(false);
        mob.setOrderedToSit(false);
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        if (canUse()) mob.discard();
    }

    @Override
    public void tick() {
        mob.getNavigation().moveTo(pointOfInterest.getX(), pointOfInterest.getY(), pointOfInterest.getZ(), 1);
        if (mob.distanceToSqr(new Vec3(pointOfInterest.getX(), mob.getY(), pointOfInterest.getZ())) < 16) updatePointOfInterest();
        timeout++;
    }

    private void updatePointOfInterest() {
        int dist = 512;
        BlockPos pos;
        do {
            pos = BlockPos.containing(mob.calculateViewVector(0, mob.getYRot()).scale(dist).add(mob.position()));
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            if (mob.level().getChunk(x, z, ChunkStatus.SURFACE, false) != null) break;
            dist -= 16;
        } while (true);
        pointOfInterest = new BlockPos(pos.getX(), 256, pos.getX());
    }
}
