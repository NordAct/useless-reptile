package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

public class FlyingDragonFlyAroundGoal<T extends URDragonEntity & FlyingDragon> extends WanderAroundGoal {
    protected final T mob;
    protected final int range;


    public FlyingDragonFlyAroundGoal(T entity, int range) {
        super(entity, 1, DEFAULT_CHANCE, false);
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
    
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = mob.getHeightMod() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = mob.getEntityWorld().getBlockState(destination.up(y));
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
            BlockPos fuzz = FuzzyPositions.localFuzz(mob.getRandom(), range, 20);
            if (fuzz.toCenterPos().length() < 10) continue;
            BlockPos result = mob.getBlockPos().add(fuzz);
            if (mob.getEntityWorld().getBlockState(result).isAir() && mob.getEntityWorld().getBlockState(result.down()).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        BlockPos target = findRandomAirSpot();
        if (target != null) return target.toCenterPos();
        else return null;
    }

}
