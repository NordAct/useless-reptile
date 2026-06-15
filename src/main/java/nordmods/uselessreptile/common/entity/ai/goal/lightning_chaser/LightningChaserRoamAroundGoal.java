package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.LightningChaser;

import java.util.EnumSet;

public class LightningChaserRoamAroundGoal extends Goal {
    private final LightningChaser entity;
    private BlockPos spot;
    private BlockPos pointPos;
    private int currentPoint = 0;

    public LightningChaserRoamAroundGoal(LightningChaser entity) {
        this.entity = entity;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (entity.isTame()) return false;
        return (entity.isChallenger() || entity.level().isThundering()) && !entity.getShouldBailOut() && !entity.hasSurrendered() && entity.getTarget() == null;
    }

    @Override
    public void start() {
        spot = getRoamingSpot();
        pointPos = spot;
    }

    private BlockPos getRoamingSpot() {
        BlockPos pos = entity.getHomePoint();
        return new BlockPos(pos.getX(), entity.level().getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 40, pos.getZ());
    }

    @Override
    public void tick() {
        if (entity.distanceToSqr(Vec3.atCenterOf(pointPos)) < entity.getWidthMod() * entity.getWidthMod() * 4) {
            pointPos = new BlockPos((int) (spot.getX() + Math.sin(Math.PI / 8 * currentPoint) * 32),
                    spot.getY(),
                    (int) (spot.getZ() + Math.cos(Math.PI / 8 * currentPoint) * 32));
            if (currentPoint < 16) currentPoint++;
            else currentPoint = 0;
        }
        Vec3 vec3d = Vec3.atCenterOf(pointPos);
        entity.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1);
    }
}
