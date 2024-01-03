package nordmods.uselessreptile.common.entity.ai.goal.lightning_chaser;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import nordmods.uselessreptile.common.entity.LightningChaserEntity;

public class LightningChaserRoamAroundGoal extends Goal {
    private final LightningChaserEntity entity;
    private BlockPos spot;
    private BlockPos pointPos;
    private int currentPoint = 0;

    public LightningChaserRoamAroundGoal(LightningChaserEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (entity.isTamed()) return false;
        return (entity.isChallenger() || entity.getWorld().isThundering()) && !entity.getShouldBailOut() && !entity.hasSurrendered() && entity.getTarget() == null;
    }

    @Override
    public void start() {
        spot = getRoamingSpot();
        pointPos = spot;
    }

    private BlockPos getRoamingSpot() {
        BlockPos pos = entity.roamingSpot != null ? entity.roamingSpot : entity.getBlockPos();
        return new BlockPos(pos.getX(), entity.getWorld().getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ()) + 48, pos.getZ());
    }

    @Override
    public void tick() {
        if (entity.squaredDistanceTo(pointPos.toCenterPos()) < entity.getWidthMod() * entity.getWidthMod() * 4) {
            pointPos = new BlockPos((int) (spot.getX() + Math.sin(Math.PI / 8 * currentPoint) * 32),
                    spot.getY(),
                    (int) (spot.getZ() + Math.cos(Math.PI / 8 * currentPoint) * 32));
            if (currentPoint < 16) currentPoint++;
            else currentPoint = 0;
        }
        Vec3d vec3d = pointPos.toCenterPos();
        entity.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1);
    }
}
