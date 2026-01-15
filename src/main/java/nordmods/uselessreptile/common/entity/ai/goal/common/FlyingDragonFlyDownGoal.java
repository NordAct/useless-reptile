package nordmods.uselessreptile.common.entity.ai.goal.common;

import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FlyingDragonFlyDownGoal<T extends URDragonEntity & FlyingDragon> extends FlyingDragonFlyAroundGoal<T> {
    public FlyingDragonFlyDownGoal(T entity, int range) {
        super(entity, range);
    }

    @Override
    public boolean canUse() {
        if (!mob.shouldFlyDown()) return false;
        if (mob.getTarget() != null) return false;
        return super.canUse();
    }
    @Override
    protected Vec3 getPosition() {
        BlockPos landingPos = landingSpot();
        if (landingPos == null) return null;
        return new Vec3(landingPos.getX(), landingPos.getY() + 1, landingPos.getZ());
    }

    @Nullable
    private BlockPos landingSpot() {
        if (mob.getY() > 320) return returnToNormalHeight();
        Level world = mob.level();

        Optional<BlockPos> closest = BlockPos.findClosestMatch(mob.blockPosition(), range, 320,
                (blockPos -> {
                    if (blockPos.getY() < world.dimensionType().minY() || blockPos.getY() > 320) return false;
                    if (!isFullCube(blockPos)) return false;
                    float height = mob.getHeightMod();
                    for (int i = 1; i <= height + 0.5; i++) {
                        BlockPos above = blockPos.above(i);
                        if (isFullCube(above) || !world.getBlockState(above).getFluidState().isEmpty()) return false;
                        if (!checkSurroundings(above)) return false;
                    }
                    return checkUnder(blockPos.above());
                }));

        BlockPos spot = closest.orElse(null);
        if (spot == null) spot = findRandomAirSpot();
        else mob.setHomePoint(spot);
        return spot;
    }

    private boolean isFullCube(BlockPos blockPos) {
        return mob.level().getBlockState(blockPos).isCollisionShapeFullBlock(mob.level(), blockPos);
    }

    private boolean checkUnder(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        int missing = 0;
        for (BlockPos pos : around) if (!isFullCube(pos.below())) missing++;
        return missing <= 3;
    }

    private boolean checkSurroundings(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        for (BlockPos pos : around) if (isFullCube(pos)) return false;
        return true;
    }
}
