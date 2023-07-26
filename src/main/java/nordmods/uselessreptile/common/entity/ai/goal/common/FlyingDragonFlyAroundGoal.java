package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.util.math.BlockPos;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

public class FlyingDragonFlyAroundGoal<T extends URDragonEntity & FlyingDragon> extends FlyGoal {

    protected final T mob;
    protected final int range;


    public FlyingDragonFlyAroundGoal(T entity, int range) {
        super(entity, 1);
        this.mob = entity;
        this.range = range;
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.isOnGround()) return false;
        return super.shouldContinue();
    }

    @Override
    public boolean canStart() {
        if (!this.mob.isFlying()) return false;
        return super.canStart();
    }

    @Nullable
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = mob.getHeightMod() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = mob.getWorld().getBlockState(destination.up(y));
            if (!blockState.getFluidState().isEmpty()) {
                adjustment = 3 + y;
                break;
            }
        }
        return destination.up(adjustment);
    }

    protected BlockPos returnToNormalHeight() {
        return mob.getBlockPos().down((int) (mob.getY() - 319));
    }

    @Nullable
    protected BlockPos findRandomAirSpot() {
        BlockPos div = null;
        for (int i = 0; i < 5; i++) {
            BlockPos fuzz = FuzzyPositions.localFuzz(mob.getRandom(), range, 5);
            BlockPos result = mob.getBlockPos().add(fuzz);
            if (mob.getWorld().getBlockState(result).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

}
