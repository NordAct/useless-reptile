package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
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

        Vec3 closest = LandRandomPos.getPos(mob, range, 320,
                (blockPos -> {
                    if (blockPos.getY() < world.dimensionType().minY() || blockPos.getY() > 320) return Double.NEGATIVE_INFINITY;
                    if (!isFullCube(blockPos)) return Double.NEGATIVE_INFINITY;
                    float height = mob.getHeightMod();
                    for (int i = 1; i <= height + 0.5; i++) {
                        BlockPos above = blockPos.above(i);
                        if (isFullCube(above) || !world.getBlockState(above).getFluidState().isEmpty()) return Double.NEGATIVE_INFINITY;
                        if (!checkSurroundings(above)) return Double.NEGATIVE_INFINITY;
                    }
                    return checkUnder(blockPos.above());
                }));

        BlockPos spot;
        if (closest == null) spot = findRandomAirSpot();
        else spot = new BlockPos((int) closest.x, (int) closest.y, (int) closest.z);

        mob.setHomePoint(spot);
        return spot;
    }

    private boolean isFullCube(BlockPos blockPos) {
        return mob.level().getBlockState(blockPos).isCollisionShapeFullBlock(mob.level(), blockPos);
    }

    private double checkUnder(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        int missing = 0;
        for (BlockPos pos : around) if (!isFullCube(pos.below())) missing++;
        return missing <= 3 ? blockPos.distSqr(mob.blockPosition()) : Double.NEGATIVE_INFINITY;
    }

    private boolean checkSurroundings(BlockPos blockPos) {
        BlockPos[] around = {blockPos.west(), blockPos.east(), blockPos.north(), blockPos.south()};
        for (BlockPos pos : around) if (isFullCube(pos)) return false;
        return true;
    }
}
