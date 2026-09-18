package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.LightningChaser;

import java.util.EnumSet;

public class LightningChaserRoamAroundGoal extends Goal {
    private final LightningChaser mob;
    private BlockPos spot;
    private BlockPos pointPos;
    private int currentPoint = 0;

    public LightningChaserRoamAroundGoal(LightningChaser mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.isTame()) return false;
        return (mob.isChallenger() || mob.level().isThundering()) && !mob.getShouldBailOut() && !mob.hasSurrendered() && mob.getTarget() == null;
    }

    @Override
    public void start() {
        spot = getRoamingSpot();
        pointPos = spot;
    }

    private BlockPos getRoamingSpot() {
        BlockPos pos = mob.getHomePoint();
        return new BlockPos(pos.getX(), mob.level().getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 40, pos.getZ());
    }

    @Override
    public void tick() {
        if (mob.distanceToSqr(pointPos.getCenter()) < mob.getBbWidth() * mob.getBbWidth() * 4) {
            pointPos = new BlockPos((int) (spot.getX() + Math.sin(Math.PI / 8 * currentPoint) * 32),
                    spot.getY(),
                    (int) (spot.getZ() + Math.cos(Math.PI / 8 * currentPoint) * 32));
            if (currentPoint < 16) currentPoint++;
            else currentPoint = 0;
        }
        Vec3 vec3d = pointPos.getCenter();
        mob.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1);
    }
}
