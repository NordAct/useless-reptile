package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FlyingDragonFlyDownGoal<T extends URDragonEntity & FlyingDragon> extends FlyingDragonFlyAroundGoal<T> {
    public FlyingDragonFlyDownGoal(T entity, int range) {
        super(entity, range);
    }

    @Override
    public boolean canStart() {
        if (this.mob.getInAirTimer() < this.mob.getMaxInAirTimer()) return false;
        if (this.mob.getTarget() != null) return false;
        return super.canStart();
    }
    @Override
    protected Vec3d getWanderTarget() {
        BlockPos landingPos = landingSpot();
        if (landingPos == null) return null;
        return new Vec3d(landingPos.getX(), landingPos.getY() + 1, landingPos.getZ());
    }

    @Nullable
    private BlockPos landingSpot() {
        if (mob.getY() > 320) return returnToNormalHeight();
        World world = mob.getWorld();

        Optional<BlockPos> closest = BlockPos.findClosest(mob.getBlockPos(), range, 320,
                (blockPos -> {
                    if (blockPos.getY() < world.getDimension().minY() || blockPos.getY() > 320) return false;
                    if (!isFullCube(blockPos)) return false;
                    float height = mob.getHeightMod();
                    for (int i = 1; i <= height + 0.5; i++) {
                        BlockPos above = blockPos.up(i);
                        if (isFullCube(above) || !world.getBlockState(above).getFluidState().isEmpty()) return false;
                        if (!checkSurroundings(above)) return false;
                    }
                    return checkUnder(blockPos.up());
                }));

        BlockPos spot = closest.orElse(null);
        if (spot == null) {
            spot = findRandomAirSpot();
        }
        return spot;
    }

    private boolean isFullCube(BlockPos blockPos) {
        return mob.getWorld().getBlockState(blockPos).isFullCube(mob.getWorld(), blockPos);
    }

    private boolean checkUnder(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        int missing = 0;
        for (BlockPos pos : around) if (!isFullCube(pos.down())) missing++;
        return missing <= 3;
    }

    private boolean checkSurroundings(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        for (BlockPos pos : around) if (isFullCube(pos)) return false;
        return true;
    }
}
