package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import nordmods.uselessreptile.common.entity.LightningChaserEntity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class LightningChaserRoamAroundGoal extends Goal {
    private final LightningChaserEntity entity;
    private BlockPos spot;
    private BlockPos pointPos;
    private int currentPoint = 0;

    public LightningChaserRoamAroundGoal(LightningChaserEntity entity) {
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
        if (entity.distanceToSqr(pointPos.getCenter()) < entity.getWidthMod() * entity.getWidthMod() * 4) {
            pointPos = new BlockPos((int) (spot.getX() + Math.sin(Math.PI / 8 * currentPoint) * 32),
                    spot.getY(),
                    (int) (spot.getZ() + Math.cos(Math.PI / 8 * currentPoint) * 32));
            if (currentPoint < 16) currentPoint++;
            else currentPoint = 0;
        }
        Vec3 vec3d = pointPos.getCenter();
        entity.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1);
    }
}
