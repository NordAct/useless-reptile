package nordmods.uselessreptile.common.entity.ai.goal.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nordmods.uselessreptile.common.entity.base.FlyingDragon;
import nordmods.uselessreptile.common.entity.base.URDragonEntity;
import org.jetbrains.annotations.Nullable;

public class FlyingDragonFlyAroundGoal<T extends URDragonEntity & FlyingDragon> extends RandomStrollGoal {
    protected final T mob;
    protected final int range;


    public FlyingDragonFlyAroundGoal(T entity, int range) {
        super(entity, 1, DEFAULT_INTERVAL, false);
        this.mob = entity;
        this.range = range;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.onGround()) return false;
        return super.canContinueToUse();
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isFlying()) return false;
        return super.canUse();
    }
    
    protected BlockPos liquidAdjustment(BlockPos destination) {
        float height = mob.getHeightMod() + 0.5f;
        int adjustment = 0;
        for (int y = 0; y < height; y++) {
            BlockState blockState = mob.level().getBlockState(destination.above(y));
            if (!blockState.getFluidState().isEmpty()) {
                adjustment = 3 + y;
                break;
            }
        }
        return destination.above(adjustment);
    }

    protected BlockPos returnToNormalHeight() {
        return mob.blockPosition().below((int) (mob.getY() - 319));
    }

    @Nullable
    protected BlockPos findRandomAirSpot() {
        BlockPos div = null;
        for (int i = 0; i < 5; i++) {
            BlockPos fuzz = RandomPos.generateRandomDirection(mob.getRandom(), range, 20);
            if (fuzz.getCenter().length() < 10) continue;
            BlockPos result = mob.blockPosition().offset(fuzz);
            if (mob.level().getBlockState(result).isAir() && mob.level().getBlockState(result.below()).isAir()) {
                div = result;
                break;
            }
        }
        if (div == null) return null;
        return liquidAdjustment(div);
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        BlockPos target = findRandomAirSpot();
        if (target != null) return target.getCenter();
        else return null;
    }

}
